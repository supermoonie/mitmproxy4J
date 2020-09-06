package com.github.supermoonie.proxy;

import com.github.supermoonie.ex.BadRequestException;
import com.github.supermoonie.util.RequestUtils;
import com.github.supermoonie.util.ResponseUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author supermoonie
 * @date 2020-08-08
 */
public class InternalProxyHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(InternalProxyHandler.class);
    private static final int SSL_FLAG = 22;

    private final InterceptContext interceptContext = new InterceptContext();
    private final Queue<Object> requestQueue = new LinkedBlockingDeque<>();
    private ConnectionStatus status = ConnectionStatus.NOT_CONNECTION;
    private ChannelFuture remoteChannelFuture;
    private final InternalProxy internalProxy;
    private ConnectionInfo connectionInfo;

    public InternalProxyHandler(InternalProxy internalProxy,
                                InterceptInitializer initializer) {
        this.internalProxy = internalProxy;
        if (null != initializer) {
            initializer.initIntercept(interceptContext.getRequestIntercepts(), interceptContext.getResponseIntercepts());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        interceptContext.setNettyClientContext(ctx);
        Channel clientChannel = ctx.channel();
        interceptContext.setClientChannel(clientChannel);
        InetSocketAddress clientAddress = (InetSocketAddress) clientChannel.remoteAddress();
        String clientHost = clientAddress.getHostString();
        int clientPort = clientAddress.getPort();
        connectionInfo = new ConnectionInfo();
        connectionInfo.setClientHost(clientHost);
        connectionInfo.setClientPort(clientPort);
        interceptContext.setConnectionInfo(connectionInfo);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("msg: {} \n", msg);
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (status.equals(ConnectionStatus.NOT_CONNECTION)) {
                ConnectionInfo info = RequestUtils.parseRemoteInfo(request, this.connectionInfo);
                if (null == info) {
                    throw new BadRequestException("Bad Request");
                }
                status = ConnectionStatus.CONNECTING;
                this.connectionInfo.setHostHeader(request.headers().get(HttpHeaderNames.HOST));
                if (HttpMethod.CONNECT.equals(request.method())) {
                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    ctx.writeAndFlush(response);
                    // 暂时移除 httpServerCodec，处理 https 握手
                    ctx.channel().pipeline().remove("httpCodec");
                    ctx.channel().pipeline().remove("decompressor");
                    ctx.channel().pipeline().remove("aggregator");
                    ReferenceCountUtil.release(msg);
                    status = ConnectionStatus.CONNECTED_WITH_CLIENT;
                    return;
                }
            }
            String separator = "/";
            if (request.uri().startsWith(separator)) {
                request.setUri((connectionInfo.isHttps() ? "https://" : "http://") + request.headers().get(HttpHeaderNames.HOST) + request.uri());
            }
            logger.debug("url: " + request.uri());
            if (connectionInfo.isHttps()) {
                SslHandler sslHandler = (SslHandler) ctx.pipeline().get("sslHandler");
                SSLSession session = sslHandler.engine().getSession();
                logger.debug("client session: {}, {}", session.getProtocol(), session.getCipherSuite());
            }
            boolean flag = interceptContext.onRequest(request);
            if (!flag) {
                return;
            }
            connectRemote(ctx.channel(), msg);
        } else if (msg instanceof HttpContent) {
            if (status.equals(ConnectionStatus.CONNECTED_WITH_CLIENT)) {
                connectRemote(ctx.channel(), msg);
            } else {
                ReferenceCountUtil.release(msg);
                status = ConnectionStatus.CONNECTING;
            }
        } else {
            ByteBuf byteBuf = (ByteBuf) msg;
            // ssl握手
            if (byteBuf.getByte(0) == SSL_FLAG) {
                connectionInfo.setHttps(true);
                int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
                String host = connectionInfo.getHostHeader().split(":")[0];
                InternalProxy.CertificateConfig certificateConfig = internalProxy.getCertificateConfig();
                SslContext sslCtx = SslContextBuilder
                        .forServer(certificateConfig.getServerPriKey(), CertificateUtil.getCert(port, host, certificateConfig)).build();
                ctx.pipeline().addFirst("httpCodec", new HttpServerCodec());
                ctx.pipeline().addLast("decompressor", new HttpContentDecompressor());
                ctx.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                ctx.pipeline().addFirst("sslHandler", sslCtx.newHandler(ctx.alloc()));
                // 重新过一遍pipeline，拿到解密后的的http报文
                ctx.pipeline().fireChannelRead(msg);
                return;
            }
            connectRemote(ctx.channel(), msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug(ctx.channel().remoteAddress().toString() + " inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FullHttpResponse response = interceptContext.onRequestException(cause);
        if (null == response) {
            ResponseUtils.sendError(ctx.channel(), cause.getMessage());
        } else {
            ctx.channel().writeAndFlush(response).addListener((ChannelFutureListener) f -> ctx.channel().close());
        }
    }

    private void connectRemote(Channel clientChannel, Object msg) {
        if (null == remoteChannelFuture) {
            if (!(msg instanceof HttpRequest)) {
                return;
            }
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(internalProxy.getWorker())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            InternalProxy.CertificateConfig certificateConfig = internalProxy.getCertificateConfig();
                            if (connectionInfo.isHttps()) {
                                ch.pipeline().addLast("sslHandler", certificateConfig
                                        .getClientSslCtx()
                                        .newHandler(ch.alloc(), connectionInfo.getRemoteHost(), connectionInfo.getRemotePort()));
                            }
                            ch.pipeline().addLast("httpCodec", new HttpClientCodec());
                            ch.pipeline().addLast("decompressor", new HttpContentDecompressor());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                            ch.pipeline().addLast("proxyClientHandle", new ChannelInboundHandlerAdapter() {

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    interceptContext.setNettyRemoteContext(ctx);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    logger.debug("received: " + msg);
                                    if (connectionInfo.isHttps()) {
                                        SslHandler sslHandler = (SslHandler) ctx.pipeline().get("sslHandler");
                                        SSLSession session = sslHandler.engine().getSession();
                                        logger.debug("remote session: {}, {}", session.getProtocol(), session.getCipherSuite());
                                    }
                                    if (msg instanceof FullHttpResponse) {
                                        FullHttpResponse response = (FullHttpResponse) msg;
                                        FullHttpResponse httpResponse = interceptContext.onResponse(response);
                                        clientChannel.writeAndFlush(httpResponse);
                                    }

                                }
                            });
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            if (remoteChannelFuture.channel().isOpen()) {
                                remoteChannelFuture.channel().close();
                            }
                            FullHttpResponse response = interceptContext.onResponseException(cause);
                            if (null == response) {
                                ResponseUtils.sendError(clientChannel, cause.getMessage());
                            } else {
                                clientChannel.writeAndFlush(response).addListener((ChannelFutureListener) f -> clientChannel.close());
                            }
                        }
                    });
            logger.debug("connect to {}:{}", connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());
            remoteChannelFuture = bootstrap.connect(connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());
            remoteChannelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    interceptContext.setRemoteChannel(future.channel());
                    logger.debug("send direct: {}", msg);
                    future.channel().writeAndFlush(msg);
                    Object obj = requestQueue.poll();
                    while (null != obj) {
                        logger.debug("send from queue: {}", obj);
                        future.channel().writeAndFlush(obj);
                        obj = requestQueue.poll();
                    }
                    status = ConnectionStatus.CONNECTED_WITH_REMOTE;
                } else {
                    if (remoteChannelFuture.channel().isOpen()) {
                        remoteChannelFuture.channel().close();
                    }
                    FullHttpResponse response = interceptContext.onResponseException(future.cause());
                    if (null == response) {
                        ResponseUtils.sendError(clientChannel, future.cause().getMessage());
                    } else {
                        clientChannel.writeAndFlush(response).addListener((ChannelFutureListener) f -> clientChannel.close());
                    }
                }
            });
        } else {
            if (status.equals(ConnectionStatus.CONNECTED_WITH_REMOTE)) {
                logger.debug("send after connected, msg: " + msg);
                remoteChannelFuture.channel().writeAndFlush(msg);
            } else {
                logger.debug("add to queue: " + msg);
                requestQueue.add(msg);
            }
        }

    }

    enum ConnectionStatus {

        /**
         * not connection
         */
        NOT_CONNECTION(0),
        /**
         * connecting
         */
        CONNECTING(1),
        /**
         * already connect with client
         */
        CONNECTED_WITH_CLIENT(2),
        /**
         * already connect with remote
         */
        CONNECTED_WITH_REMOTE(3);

        private final int code;

        ConnectionStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
