package com.github.supermoonie.proxy.intercept.req;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.intercept.InterceptContext;
import com.github.supermoonie.proxy.intercept.res.LastResponseIntercept;
import com.github.supermoonie.proxy.intercept.res.ResponseInterceptPipeline;
import com.github.supermoonie.util.CertificateUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author supermoonie
 * @since 2020/8/11
 */
public class LastRequestIntercept extends AbstractRequestIntercept {

    private static final Logger logger = LoggerFactory.getLogger(LastRequestIntercept.class);

    private final Queue<Object> requestQueue = new LinkedBlockingDeque<>();

    private boolean connectionFlag = false;

    private final ResponseInterceptPipeline responseInterceptPipeline;

    public LastRequestIntercept() {
        this(new ResponseInterceptPipeline());
    }

    public LastRequestIntercept(ResponseInterceptPipeline responseInterceptPipeline) {
        this.responseInterceptPipeline = responseInterceptPipeline;
        this.responseInterceptPipeline.addLast(new LastResponseIntercept());
    }

    @Override
    public boolean onRequest(InterceptContext context, FullHttpRequest request) {
        System.out.println("onRequest class: " + request.getClass().getName() + ", msg: " + request);
        if (null == context.getRemoteChannel()) {
            Bootstrap b = new Bootstrap();
            b.group(context.getClientChannel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ConnectionInfo connectionInfo = context.getConnectionInfo();
                            System.out.println("remoteHost: " + connectionInfo.getRemoteHost() + ", remotePort: " + connectionInfo.getRemotePort());
                            ch.pipeline().addLast(CertificateUtil.getClientSslContext().newHandler(
                                    ch.alloc(),
                                    connectionInfo.getRemoteHost(),
                                    connectionInfo.getRemotePort())
                            );
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpContentDecompressor());
                            ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024 * 5));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object response) throws Exception {
                                    System.out.println("response class: " + response.getClass().getName() + ", msg: " + response);
                                    if (response instanceof FullHttpResponse) {
                                        FullHttpResponse res = (FullHttpResponse) response;
                                        System.out.println("uri: " + request.uri() + ", response content: " + res.content().toString(CharsetUtil.UTF_8));
                                        responseInterceptPipeline.onResponse(context, res);
                                    }
                                    if (!context.getClientChannel().isOpen()) {
                                        ReferenceCountUtil.release(response);
                                    }
                                }
                            });
                        }
                    });
            String remoteHost = context.getConnectionInfo().getRemoteHost();
            int port = context.getConnectionInfo().getRemotePort();
            ChannelFuture f = b.connect(remoteHost, port);
            Channel remoteChannel = f.channel();
            context.setRemoteChannel(remoteChannel);
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    System.out.println("send class: " + request.getClass().getName() + ", msg: " + request);
                    future.channel().writeAndFlush(request);
                    Object obj = requestQueue.poll();
                    while (null != obj) {
                        System.out.println("send from requestQueue obj: " + obj.getClass().getName() + ", obj: " + obj);
                        future.channel().writeAndFlush(obj);
                        obj = requestQueue.poll();
                    }
                    connectionFlag = true;
                } else {
                    context.getClientChannel().close();
                }
            });
        } else {
            if (connectionFlag) {
                System.out.println("send from requestQueue... class: " + request.getClass().getName() + ", msg: " + request);
                context.getRemoteChannel().writeAndFlush(request);
            } else {
                System.out.println("add to requestQueue... class: " + request.getClass().getName() + ", msg: " + request);
                requestQueue.add(request);
            }
        }
        return true;
    }

    @Override
    public boolean onException(InterceptContext ctx, FullHttpRequest request, Exception ex) throws Exception {
        return true;
    }
}
