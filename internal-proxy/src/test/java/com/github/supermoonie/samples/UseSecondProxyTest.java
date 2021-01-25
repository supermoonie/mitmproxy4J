package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.SecondProxyConfig;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
public class UseSecondProxyTest {

    public static void main(String[] args) {
        SecondProxyConfig secondProxyConfig = new SecondProxyConfig();
        secondProxyConfig.setHost("127.0.0.1");
        secondProxyConfig.setPort(7899);
        secondProxyConfig.setProxyType(ProxyType.HTTP);
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            LoggingIntercept loggingIntercept = new LoggingIntercept();
            requestIntercepts.put("logging-intercept", loggingIntercept);
            responseIntercepts.put("logging-intercept", loggingIntercept);
            requestIntercepts.put("--", new ConfigurableIntercept() {
                @Override
                public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
                    ctx.getConnectionInfo().setUseSecondProxy(true);
                    ctx.getConnectionInfo().setSecondProxyConfig(secondProxyConfig);
                    return null;
                }
            });
        });

        proxy.setWorkerThreads(new NioEventLoopGroup(32));
        proxy.setBossThreads(new NioEventLoopGroup(2));
        proxy.setProxyThreads(new NioEventLoopGroup(32));
        proxy.setPort(10801);
        proxy.start();
    }
}
