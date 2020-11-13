package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
public class SecondProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
//            LoggingIntercept loggingIntercept = new LoggingIntercept();
//            requestIntercepts.put("logging-intercept", loggingIntercept);
//            responseIntercepts.put("logging-intercept", loggingIntercept);
        });
        InternalProxy.SecondProxyConfig secondProxyConfig = new InternalProxy.SecondProxyConfig();
        secondProxyConfig.setHost("127.0.0.1");
        secondProxyConfig.setPort(8888);
        secondProxyConfig.setProxyType(ProxyType.HTTP);
        proxy.setSecondProxyConfig(secondProxyConfig);
        proxy.setWorkerThreads(new NioEventLoopGroup(16));
        proxy.setBossThreads(new NioEventLoopGroup(1));
        proxy.setProxyThreads(new NioEventLoopGroup(16));
        proxy.setPort(10801);
        proxy.start();
    }
}
