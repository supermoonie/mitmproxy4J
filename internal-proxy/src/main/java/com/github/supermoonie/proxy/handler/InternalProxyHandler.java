package com.github.supermoonie.proxy.handler;

import com.github.supermoonie.constant.ConnectionState;
import com.github.supermoonie.ex.BadRequestException;
import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.intercept.InterceptContext;
import com.github.supermoonie.proxy.intercept.req.LastRequestIntercept;
import com.github.supermoonie.proxy.intercept.req.RequestInterceptPipeline;
import com.github.supermoonie.proxy.intercept.res.ResponseInterceptPipeline;
import com.github.supermoonie.util.CertificateUtil;
import com.github.supermoonie.util.RequestUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;

/**
 * @author supermoonie
 * @date 2020-08-08
 */
public class InternalProxyHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(InternalProxyHandler.class);

    private static final int SSL_FLAG = 22;

    private final RequestInterceptPipeline requestInterceptPipeline = new RequestInterceptPipeline();

    private final ResponseInterceptPipeline responseInterceptPipeline = new ResponseInterceptPipeline();

    private final InterceptContext interceptContext = new InterceptContext(requestInterceptPipeline, responseInterceptPipeline);

    private ConnectionState state = ConnectionState.NOT_CONNECTION;

    private final String caFileName;

    private final String keyFileName;

    public InternalProxyHandler(String caFileName, String keyFileName, InternalProxyHandlerInitializer initializer) {
        this.caFileName = caFileName;
        this.keyFileName = keyFileName;
        if (null != initializer) {
            initializer.initInterceptPipeline(requestInterceptPipeline, responseInterceptPipeline);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel clientChannel = ctx.channel();
        InetSocketAddress clientAddress = (InetSocketAddress) clientChannel.remoteAddress();
        String clientHost = clientAddress.getHostString();
        int clientPort = clientAddress.getPort();
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setClientHost(clientHost);
        connectionInfo.setClientPort(clientPort);
        interceptContext.setConnectionInfo(connectionInfo);
        interceptContext.setClientChannel(clientChannel);
        requestInterceptPipeline.addLast(new LastRequestIntercept(responseInterceptPipeline));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("class: {}, msg: {}", msg.getClass().getName(), msg);
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            ConnectionInfo connectionInfo = RequestUtils.parseRemoteInfo(request, interceptContext.getConnectionInfo());
            if (null == connectionInfo) {
                logger.warn("bad request, msg: {}", msg);
                ctx.channel().close();
                requestInterceptPipeline.onException(interceptContext, null, new BadRequestException(msg.toString()));
                return;
            }
            if (state == ConnectionState.NOT_CONNECTION) {
                state = ConnectionState.CONNECTING;
                connectionInfo.setHostHeader(request.headers().get(HttpHeaderNames.HOST));
                if (HttpMethod.CONNECT.equals(request.method())) {
                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    ctx.writeAndFlush(response);
                    // 暂时移除 httpServerCodec，处理 https 握手
                    ctx.channel().pipeline().remove("httpServerCodec");
                    ReferenceCountUtil.release(msg);
                    state = ConnectionState.CONNECTED_WITH_CLIENT;
                    return;
                }
            }
            String separator = "/";
            if (request.uri().startsWith(separator)) {
                request.setUri((connectionInfo.isHttps() ? "https://" : "http://") + connectionInfo.getHostHeader() + request.uri());
            }
            if (msg instanceof FullHttpRequest) {
                SslHandler sslHandler = (SslHandler) ctx.pipeline().get("sslHandler");
                SSLSession session = sslHandler.engine().getSession();
                logger.info("session: {}, {}", session.getProtocol(), session.getCipherSuite());
                requestInterceptPipeline.onRequest(interceptContext, (FullHttpRequest) msg);
            }
            state = ConnectionState.CONNECTED_WITH_REMOTE;
        } else {
            ByteBuf byteBuf = (ByteBuf) msg;
            if (SSL_FLAG == byteBuf.getByte(0)) {
                ConnectionInfo connectionInfo = interceptContext.getConnectionInfo();
                connectionInfo.setHttps(true);
                int port = connectionInfo.getClientPort();
                String host = connectionInfo.getClientHost();
                SslContext sslCtx = SslContextBuilder
                        .forServer(CertificateUtil.getServerPrivateKey(), CertificateUtil.getCert(
                                port,
                                host,
                                CertificateUtil.loadCa(caFileName),
                                CertificateUtil.loadCaPrivateKey(keyFileName))
                        ).build();
                ctx.pipeline().addFirst("httpServerCodec", new HttpServerCodec());
                SslHandler sslHandler = sslCtx.newHandler(ctx.alloc());
                ctx.pipeline().addFirst("sslHandler", sslHandler);
                ctx.pipeline().fireChannelRead(msg);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info(ctx.channel().remoteAddress().toString() + " inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }
}
