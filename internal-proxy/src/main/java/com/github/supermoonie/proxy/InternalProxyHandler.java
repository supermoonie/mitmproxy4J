package com.github.supermoonie.proxy;

import com.github.supermoonie.proxy.dns.IntervalAddressResolverGroup;
import com.github.supermoonie.proxy.ex.AuthorizationFailedException;
import com.github.supermoonie.proxy.ex.BadRequestException;
import com.github.supermoonie.proxy.util.RequestUtils;
import com.github.supermoonie.proxy.util.ResponseUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
    private volatile ConnectionStatus status = ConnectionStatus.NOT_CONNECTED;
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

    private void verifyAuth(HttpRequest request) {
        if (this.internalProxy.isAuth() && null != this.internalProxy.getUsername() && null != this.internalProxy.getPassword()) {
            String auth = "Basic " + Base64.getEncoder().encodeToString((this.internalProxy.getUsername() + ":" + this.internalProxy.getPassword()).getBytes(StandardCharsets.UTF_8));
            String authorization = request.headers().get(HttpHeaderNames.PROXY_AUTHORIZATION);
            if (null == authorization || !authorization.equals(auth)) {
                throw new AuthorizationFailedException("Authorization Failed!");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        interceptContext.setNettyClientContext(ctx);
        Channel clientChannel = ctx.channel();
        interceptContext.setClientChannel(clientChannel);
        InetSocketAddress clientAddress = (InetSocketAddress) clientChannel.remoteAddress();
        logger.debug("{} active", clientAddress.toString());
        String clientHost = clientAddress.getHostString();
        int clientPort = clientAddress.getPort();
        connectionInfo = new ConnectionInfo();
        connectionInfo.setClientHost(clientHost);
        connectionInfo.setClientPort(clientPort);
        interceptContext.setConnectionInfo(connectionInfo);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("from client {} msg: {} \n", ctx.channel().remoteAddress().toString(), msg);
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            interceptContext.setRequest(request);
            if (status.equals(ConnectionStatus.NOT_CONNECTED)) {
                ConnectionInfo info = RequestUtils.parseRemoteInfo(request, this.connectionInfo);
                if (null == info) {
                    ReferenceCountUtil.release(msg);
                    throw new BadRequestException("Bad Request!");
                }
                status = ConnectionStatus.CONNECTING;
                this.connectionInfo.setHostHeader(request.headers().get(HttpHeaderNames.HOST));
                if (HttpMethod.CONNECT.equals(request.method())) {
                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    ctx.writeAndFlush(response);
                    logger.debug("be connected by {}", ctx.channel().remoteAddress().toString());
                    // 暂时移除 httpServerCodec，处理 https 握手
                    ctx.pipeline().remove("httpCodec");
                    ctx.pipeline().remove("decompressor");
                    ctx.pipeline().remove("aggregator");
                    ReferenceCountUtil.release(msg);
                    status = ConnectionStatus.CONNECTED_WITH_CLIENT;
                    return;
                }
            }
            logger.debug("pipeline: " + ctx.pipeline());
            verifyAuth(request);
            String separator = "/";
            if (request.uri().startsWith(separator)) {
                connectionInfo.setUrl((connectionInfo.isHttps() ? "https://" : "http://") + connectionInfo.getRemoteHost() + request.uri());
            }
            logger.debug("url: " + request.uri());
            if (connectionInfo.isHttps()) {
                SslHandler sslHandler = (SslHandler) ctx.pipeline().get("sslHandler");
                SSLSession session = sslHandler.engine().getSession();
                logger.debug("client session: {}, {}", session.getProtocol(), session.getCipherSuite());
            }
            boolean flag = interceptContext.onRequest(request);
            if (!flag) {
                ReferenceCountUtil.release(msg);
                return;
            }
            logger.debug(connectionInfo.toString());
            connectRemote(ctx.channel(), request);
        } else if (msg instanceof HttpContent) {
            if (msg instanceof LastHttpContent) {
                connectionInfo.setFinished(true);
            }
            HttpContent content = (HttpContent) msg;
            connectRemote(ctx.channel(), content);
        } else {
            ByteBuf byteBuf = (ByteBuf) msg;
            // ssl握手
            if (byteBuf.getByte(0) == SSL_FLAG) {
                logger.debug("handshake with {}:{}", connectionInfo.getClientHost(), connectionInfo.getClientPort());
                connectionInfo.setHttps(true);
                int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
                String host = connectionInfo.getHostHeader().split(":")[0];
                InternalProxy.CertificateConfig certificateConfig = internalProxy.getCertificateConfig();
                SslContext sslCtx = SslContextBuilder
                        .forServer(certificateConfig.getServerPriKey(), CertificateUtil.getCert(port, host, certificateConfig)).build();
                ctx.pipeline().addFirst("httpCodec", new HttpServerCodec());
                ctx.pipeline().addAfter("httpCodec", "decompressor", new HttpContentDecompressor());
                ctx.pipeline().addAfter("decompressor", "aggregator", new HttpObjectAggregator(internalProxy.getMaxContentSize()));
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
        if (null != remoteChannelFuture && remoteChannelFuture.channel().isOpen()) {
            remoteChannelFuture.channel().close();
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FullHttpResponse response = interceptContext.onRequestException(interceptContext.getRequest(), cause);
        if (null == response) {
            ResponseUtils.sendError(ctx.channel(), cause.getMessage());
        } else {
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void connectRemote(Channel clientChannel, Object msg) {
        if (null == remoteChannelFuture) {
            if (!(msg instanceof HttpRequest)) {
                return;
            }
            HttpRequest request = (HttpRequest) msg;
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(internalProxy.getProxyThreads())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new WriteTimeoutHandler(600));
                            ch.pipeline().addLast(new ReadTimeoutHandler(600));
                            if (internalProxy.isTrafficShaping()) {
                                ch.pipeline().addLast("trafficShapingHandler", internalProxy.getTrafficShapingHandler());
                            }
                            if (connectionInfo.isUseSecondProxy()) {
                                ProxyHandler proxyHandler = ProxyHandleFactory.build(internalProxy.getSecondProxyConfig());
                                if (null != proxyHandler) {
                                    logger.debug(request.uri() + " through second proxy");
                                    ch.pipeline().addLast("proxyHandler", proxyHandler);
                                }
                            }
                            InternalProxy.CertificateConfig certificateConfig = internalProxy.getCertificateConfig();
                            if (connectionInfo.isHttps()) {
                                logger.debug("{}:{} is https", connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());
                                ch.pipeline().addLast("sslHandler", certificateConfig
                                        .getClientSslCtx()
                                        .newHandler(ch.alloc(), connectionInfo.getRemoteHost(), connectionInfo.getRemotePort()));
                            }
                            ch.pipeline().addLast("httpCodec", new HttpClientCodec());
                            ch.pipeline().addLast("decompressor", new HttpContentDecompressor());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(internalProxy.getMaxContentSize()));
                            ch.pipeline().addLast("proxyClientHandle", new ChannelInboundHandlerAdapter() {

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    interceptContext.setNettyRemoteContext(ctx);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if (connectionInfo.isHttps()) {
                                        SslHandler sslHandler = (SslHandler) ctx.pipeline().get("sslHandler");
                                        SSLSession session = sslHandler.engine().getSession();
                                        logger.debug("remote session: {}, {}", session.getProtocol(), session.getCipherSuite());
                                    }
                                    logger.debug("received: " + msg);
                                    if (msg instanceof FullHttpResponse) {
                                        FullHttpResponse response = (FullHttpResponse) msg;
                                        interceptContext.setFullHttpResponse(response);
                                        FullHttpResponse httpResponse = interceptContext.onResponse(request, response);
                                        if ("bytes".equals(httpResponse.headers().get(HttpHeaderNames.ACCEPT_RANGES))) {
                                            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
                                        }
                                        clientChannel.writeAndFlush(httpResponse);
                                    } else {
                                        clientChannel.writeAndFlush(msg);
                                    }
                                    ctx.channel().close();
                                    clientChannel.close();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    FullHttpResponse response = interceptContext.onResponseException(request, null, cause);
                                    if (null == response) {
                                        ResponseUtils.sendError(clientChannel, cause.getMessage());
                                    } else {
                                        clientChannel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                                        ReferenceCountUtil.release(response);
                                    }
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    logger.debug("{}:{} inactive", connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());
                                    clientChannel.close();
                                }
                            });
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            FullHttpResponse response = interceptContext.onResponseException(request, interceptContext.getFullHttpResponse(), cause);
                            if (null == response) {
                                ResponseUtils.sendError(clientChannel, cause.getMessage());
                            } else {
                                clientChannel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                            }
                        }
                    });
            logger.debug("connect to {}:{}", connectionInfo.getRemoteHost(), connectionInfo.getRemotePort());
            bootstrap.resolver(IntervalAddressResolverGroup.INSTANCE);
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
                    FullHttpResponse response = interceptContext.onResponseException(request, interceptContext.getFullHttpResponse(), future.cause());
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
        NOT_CONNECTED(0),
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
