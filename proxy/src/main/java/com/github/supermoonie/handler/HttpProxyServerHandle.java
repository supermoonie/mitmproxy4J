package com.github.supermoonie.handler;

import com.github.supermoonie.crt.CertPool;
import com.github.supermoonie.exception.HttpProxyExceptionHandle;
import com.github.supermoonie.intercept.HttpProxyIntercept;
import com.github.supermoonie.intercept.HttpProxyInterceptInitializer;
import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import com.github.supermoonie.intercept.HttpTunnelIntercept;
import com.github.supermoonie.proxy.ProxyHandleFactory;
import com.github.supermoonie.proxy.SecondProxyConfig;
import com.github.supermoonie.server.HttpProxyServerConfig;
import com.github.supermoonie.util.ProtoUtil;
import com.github.supermoonie.util.ProtoUtil.RequestProto;
import com.github.supermoonie.util.ResponseUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.resolver.NoopAddressResolverGroup;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * @author wangc
 */
public class HttpProxyServerHandle extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(HttpProxyServerHandle.class);

    private ChannelFuture cf;
    private String host;
    private int port;
    private boolean isSsl = false;
    private int status = 0;
    private final HttpProxyServerConfig serverConfig;
    private final SecondProxyConfig secondProxyConfig;
    private final HttpProxyInterceptInitializer interceptInitializer;
    private HttpProxyInterceptPipeline interceptPipeline;
    private final HttpTunnelIntercept tunnelIntercept;
    private final HttpProxyExceptionHandle exceptionHandle;
    /**
     * 在连接目标服务器的过程中，用于容纳客户端的请求对象，
     * 建立与目标服务器的连接后，将对象发送至目标服务器
     */
    private final List<Object> requestList = new LinkedList<>();
    private boolean isConnect;

    public HttpProxyServerHandle(HttpProxyServerConfig serverConfig, HttpProxyInterceptInitializer interceptInitializer, HttpTunnelIntercept tunnelIntercept, SecondProxyConfig secondProxyConfig, HttpProxyExceptionHandle exceptionHandle) {
        this.serverConfig = serverConfig;
        this.secondProxyConfig = secondProxyConfig;
        this.interceptInitializer = interceptInitializer;
        this.tunnelIntercept = tunnelIntercept;
        this.exceptionHandle = exceptionHandle;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        log.info("class: {}, msg: {}", msg.getClass().getName(), msg);
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            // 第一次建立连接取host和端口号和处理代理握手
            if (status == 0) {
                log.debug("---------- CONNECT uri: {} ----------", request.uri());
                RequestProto requestProto = ProtoUtil.getRequestProto(request);
                // bad request
                if (requestProto == null) {
                    log.warn("---------- bad request uri: {} ----------", msg.toString());
                    ctx.channel().close();
                    return;
                }
                status = 1;
                this.host = requestProto.getHost();
                this.port = requestProto.getPort();
                // 建立代理握手
                if (HttpMethod.CONNECT.name().equalsIgnoreCase(request.method().name())) {
                    log.debug("---------- connecting {}:{} ----------", requestProto.getHost(), requestProto.getPort());
                    status = 2;
                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    ctx.writeAndFlush(response);
                    ctx.channel().pipeline().remove("httpCodec");
                    // fix issue #42
                    ReferenceCountUtil.release(msg);
                    log.debug("---------- CONNECTED uri: {} ----------", request.uri());
                    return;
                }
            }
            String httpKeyword = "http://";
            String httpsKeyword = "https://";
            if (!request.uri().startsWith(httpKeyword) && !request.uri().startsWith(httpsKeyword)) {
                String baseUri = (this.isSsl ? "https://" : "http://")
                        + this.host + (this.port == 80 || this.port == 443 ? "" : ":" + this.port);
                request.setUri(baseUri + request.uri());
            }
            if (msg instanceof DefaultHttpRequest) {
                log.debug("---------- BUILD INTERCEPT PIPELINE, uri: {} ----------", request.uri());
                interceptPipeline = buildPipeline();
                interceptPipeline.setRequestProto(new RequestProto(this.host, this.port, this.isSsl));
                log.debug("---------- HAS BUILD INTERCEPT PIPELINE, uri: {} ----------", request.uri());
            }
            interceptPipeline.beforeRequest(ctx.channel(), request);
        } else if (msg instanceof HttpContent) {
            if (status != 2) {
                interceptPipeline.beforeRequest(ctx.channel(), (HttpContent) msg);
            } else {
                ReferenceCountUtil.release(msg);
                status = 1;
            }
        } else {
            // ssl和websocket的握手处理
            if (serverConfig.isHandleSsl()) {
                ByteBuf byteBuf = (ByteBuf) msg;
                int sslHandshakeFlag = 22;
                // ssl握手
                if (byteBuf.getByte(0) == sslHandshakeFlag) {
                    isSsl = true;
                    int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
                    SslContext sslCtx = SslContextBuilder
                            .forServer(serverConfig.getServerPriKey(), CertPool.getCert(port, this.host, serverConfig)).build();
                    ctx.pipeline().addFirst("httpCodec", new HttpServerCodec());
                    ctx.pipeline().addFirst("sslHandle", sslCtx.newHandler(ctx.alloc()));
                    // 重新过一遍pipeline，拿到解密后的的http报文
                    ctx.pipeline().fireChannelRead(msg);
                    return;
                }
            }
            handleProxyData(ctx.channel(), msg, false);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (cf != null) {
            cf.channel().close();
        }
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cf != null) {
            cf.channel().close();
        }
        ctx.channel().close();
        exceptionHandle.beforeCatch(ctx.channel(), cause);
    }

    private void handleProxyData(Channel channel, Object msg, boolean isHttp) {
        // ChannelFuture 为null，说明还未和目标服务器建立连接
        if (cf == null) {
            // connection异常 还有HttpContent进来，不转发
            if (isHttp && !(msg instanceof HttpRequest)) {
                return;
            }
            // 构建二级代理处理器
            ProxyHandler proxyHandler = ProxyHandleFactory.build(secondProxyConfig);
            /*
             * 添加SSL client hello的Server Name Indication extension(SNI扩展) 有些服务器对于client
             * hello不带SNI扩展时会直接返回Received fatal alert: handshake_failure(握手错误)
             * 例如：https://cdn.mdn.mozilla.net/static/img/favicon32.7f3da72dcea1.png
             */
            RequestProto requestProto;
            if (!isHttp) {
                requestProto = new RequestProto(host, port, isSsl);
                if (this.tunnelIntercept != null) {
                    this.tunnelIntercept.handle(requestProto);
                }
            } else {
                requestProto = interceptPipeline.getRequestProto();
                HttpRequest httpRequest = (HttpRequest) msg;
                // 检查requestProto是否有修改
                RequestProto requestProto1 = ProtoUtil.getRequestProto(httpRequest);
                if (null == requestProto1) {
                    throw new NullPointerException("requestProto1 is null");
                }
                if (!requestProto1.equals(requestProto)) {
                    // 更新Host请求头
                    boolean updateHost = (requestProto.getSsl() && requestProto.getPort() == 443)
                            || (!requestProto.getSsl() && requestProto.getPort() == 80);
                    if (updateHost) {
                        httpRequest.headers().set(HttpHeaderNames.HOST, requestProto.getHost());
                    } else {
                        httpRequest.headers().set(HttpHeaderNames.HOST, requestProto.getHost() + ":" + requestProto.getPort());
                    }
                }
            }
            ChannelInitializer channelInitializer = isHttp ? new HttpProxyInitializer(channel, requestProto, proxyHandler)
                    : new TunnelProxyInitializer(channel, proxyHandler);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(serverConfig.getProxyLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(channelInitializer);
            if (secondProxyConfig != null) {
                // 代理服务器解析DNS和连接
                bootstrap.resolver(NoopAddressResolverGroup.INSTANCE);
            }
            cf = bootstrap.connect(requestProto.getHost(), requestProto.getPort());
            cf.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                    synchronized (requestList) {
                        requestList.forEach(obj -> future.channel().writeAndFlush(obj));
                        requestList.clear();
                        isConnect = true;
                    }
                } else {
                    requestList.forEach(ReferenceCountUtil::release);
                    requestList.clear();
                    Throwable cause = future.cause();
                    String body = "<h1>mitmproxy4J Error Report:</h1><h3>" + cause.getMessage() + "</h3>";
                    HttpResponse httpResponse = ResponseUtils.htmlResponse(body, HttpResponseStatus.SERVICE_UNAVAILABLE);
                    interceptPipeline.afterResponse(channel, future.channel(), httpResponse);
                    interceptPipeline.afterException(future.channel(), channel, cause);
                    future.channel().close();
                    channel.close();
                }
            });
        } else {
            synchronized (requestList) {
                if (isConnect) {
                    cf.channel().writeAndFlush(msg);
                } else {
                    requestList.add(msg);
                }
            }
        }
    }

    private HttpProxyInterceptPipeline buildPipeline() {
        HttpProxyInterceptPipeline interceptPipeline = new HttpProxyInterceptPipeline(firstIntercept);
        interceptInitializer.init(interceptPipeline);
        return interceptPipeline;
    }

    /**
     * first intercept for handle proxy data
     */
    private final HttpProxyIntercept firstIntercept = new HttpProxyIntercept() {
        @Override
        public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
            handleProxyData(clientChannel, httpRequest, true);
        }

        @Override
        public void beforeRequest(Channel clientChannel, HttpContent httpContent, HttpProxyInterceptPipeline pipeline) {
            handleProxyData(clientChannel, httpContent, true);
        }

        @Override
        public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse,
                                  HttpProxyInterceptPipeline pipeline) {
            clientChannel.writeAndFlush(httpResponse);
            if (HttpHeaderValues.WEBSOCKET.toString().equals(httpResponse.headers().get(HttpHeaderNames.UPGRADE))) {
                // websocket转发原始报文
                proxyChannel.pipeline().remove("httpCodec");
                clientChannel.pipeline().remove("httpCodec");
            }
        }

        @Override
        public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpContent httpContent,
                                  HttpProxyInterceptPipeline pipeline) {
            clientChannel.writeAndFlush(httpContent);
        }
    };

    public HttpProxyServerConfig getServerConfig() {
        return serverConfig;
    }

    public HttpProxyInterceptPipeline getInterceptPipeline() {
        return interceptPipeline;
    }

    public HttpProxyExceptionHandle getExceptionHandle() {
        return exceptionHandle;
    }
}
