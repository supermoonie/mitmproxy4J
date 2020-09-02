package com.github.supermoonie.proxy;

import com.github.supermoonie.ex.InternalProxyCloseException;
import com.github.supermoonie.ex.InternalProxyStartException;
import com.github.supermoonie.proxy.handler.InternalProxyHandler;
import com.github.supermoonie.proxy.handler.InternalProxyHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author supermoonie
 * @date 2020-08-08
 */
public class InternalProxy {

    private final NioEventLoopGroup boss;

    private final NioEventLoopGroup worker;

    private final int port;

    private ChannelFuture future;

    private final InternalProxyHandlerInitializer initializer;

    public InternalProxy(int nBoosThread, int nWorkerThread, int port, InternalProxyHandlerInitializer initializer) {
        this.boss = new NioEventLoopGroup(nBoosThread);
        this.worker = new NioEventLoopGroup(nWorkerThread);
        this.port = port;
        this.initializer = initializer;
    }

    public InternalProxy start() {
        ServerBootstrap b = new ServerBootstrap();
        try {
            future = b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("httpServerCodec", new HttpServerCodec());
//                            ch.pipeline().addLast(new HttpContentDecompressor());
//                            ch.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
                            ch.pipeline().addLast(InternalProxyHandler.class.getSimpleName(), new InternalProxyHandler("ca.crt", "ca_private.pem", initializer));
                        }
                    }).bind(port).sync();
            return this;
        } catch (InterruptedException e) {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
            throw new InternalProxyStartException(e);
        }
    }

    public void close() {
        if (null != future && future.channel().isOpen()) {
            try {
                future.channel().close().sync();
            } catch (InterruptedException e) {
                throw new InternalProxyCloseException(e);
            } finally {
                worker.shutdownGracefully();
                boss.shutdownGracefully();
            }
        }
    }

}
