package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.internal.SocketUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
public class SecondProxyTest {

    private static final int DNS_DEFAULT_PORT = 53;

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
//            LoggingIntercept loggingIntercept = new LoggingIntercept();
//            requestIntercepts.put("logging-intercept", loggingIntercept);
//            responseIntercepts.put("logging-intercept", loggingIntercept);
        });
        InternalProxy.DnsNameResolverConfig dnsNameResolverConfig = proxy.getDnsNameResolverConfig();
        dnsNameResolverConfig.setUseSystemDefault(true);
        List<InetSocketAddress> dnsServerList = dnsNameResolverConfig.getDnsServerList();
        dnsServerList.add(SocketUtils.socketAddress("8.8.8.8", DNS_DEFAULT_PORT));
        dnsServerList.add(SocketUtils.socketAddress("8.8.4.4", DNS_DEFAULT_PORT));
        dnsServerList.add(SocketUtils.socketAddress("114.114.114.114", DNS_DEFAULT_PORT));
        InternalProxy.SecondProxyConfig secondProxyConfig = new InternalProxy.SecondProxyConfig();
        secondProxyConfig.setHost("127.0.0.1");
        secondProxyConfig.setPort(7899);
        secondProxyConfig.setProxyType(ProxyType.HTTP);
        proxy.setSecondProxyConfig(secondProxyConfig);
        proxy.setWorkerThreads(new NioEventLoopGroup(16));
        proxy.setBossThreads(new NioEventLoopGroup(1));
        proxy.setProxyThreads(new NioEventLoopGroup(16));
        proxy.setPort(10801);
        proxy.start();
    }
}
