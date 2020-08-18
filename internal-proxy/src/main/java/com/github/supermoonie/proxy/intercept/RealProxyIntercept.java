package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.util.CertificateUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author supermoonie
 * @since 2020/8/11
 */
public class RealProxyIntercept extends AbstractIntercept {

    private final Queue<Object> requestQueue = new LinkedBlockingDeque<>();

    private boolean connectionFlag = false;

    @Override
    public void onActive(InterceptContext ctx) {

    }

    @Override
    public boolean onRequest(InterceptContext context, Object msg) {
        System.out.println("onRequest class: " + msg.getClass().getName() + ", msg: " + msg);
        if (null == context.getRemoteChannel()) {
            Bootstrap b = new Bootstrap();
            b.group(context.getClientChannel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ConnectionInfo connectionInfo = context.getConnectionInfo();
                            ch.pipeline().addLast(CertificateUtil.getClientSslContext().newHandler(
                                    ch.alloc(),
                                    connectionInfo.getRemoteHost(),
                                    connectionInfo.getRemotePort())
                            );
                            ch.pipeline().addLast(HttpClientCodec.class.getName(), new HttpClientCodec());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("response class: " + msg.getClass().getName() + ", msg: " + msg);
                                    if (!context.getClientChannel().isOpen()) {
                                        ReferenceCountUtil.release(msg);
                                        return;
                                    }
                                    onResponse(context, msg);
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
                    System.out.println("send class: " + msg.getClass().getName() + ", msg: " + msg);
                    future.channel().writeAndFlush(msg);
                    synchronized (requestQueue) {
                        Object obj = requestQueue.poll();
                        while (null != obj) {
                            System.out.println("send from requestQueue obj: " + obj.getClass().getName() + ", obj: " + obj);
                            future.channel().writeAndFlush(obj);
                            obj = requestQueue.poll();
                        }
                        connectionFlag = true;
                    }

                } else {
                    context.getClientChannel().close();
                }
            });
        } else {
            synchronized (requestQueue) {
                if (connectionFlag) {
                    System.out.println("send from requestQueue... class: " + msg.getClass().getName() + ", msg: " + msg);
                    context.getRemoteChannel().writeAndFlush(msg);
                } else {
                    System.out.println("add to requestQueue... class: " + msg.getClass().getName() + ", msg: " + msg);
                    requestQueue.add(msg);
                }
            }
        }

        return true;
    }

    @Override
    public void onResponse(InterceptContext ctx, Object msg) {
        ctx.getClientChannel().writeAndFlush(msg);
    }
}
