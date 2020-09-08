package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
public class UseSecondProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            LoggingIntercept loggingIntercept = new LoggingIntercept();
            requestIntercepts.put("logging-intercept", loggingIntercept);
            responseIntercepts.put("logging-intercept", loggingIntercept);
            ConfigurableIntercept configurableIntercept = new ConfigurableIntercept();
            List<String> useSecondProxyHostList = configurableIntercept.getUseSecondProxyHostList();
            useSecondProxyHostList.add("www.youtube.com");
            useSecondProxyHostList.add("r4---sn-npoe7nes.googlevideo.com");
            requestIntercepts.put("use-second-proxy-host-intercept", configurableIntercept);
        });
        InternalProxy.SecondProxyConfig secondProxyConfig = new InternalProxy.SecondProxyConfig();
        secondProxyConfig.setHost("127.0.0.1");
        secondProxyConfig.setPort(7890);
        secondProxyConfig.setProxyType(ProxyType.HTTP);
        proxy.setSecondProxyConfig(secondProxyConfig);
        proxy.setWorkerThreads(new NioEventLoopGroup(32));
        proxy.setBossThreads(new NioEventLoopGroup(2));
        proxy.setProxyThreads(new NioEventLoopGroup(32));
        proxy.setPort(10801);
        proxy.start();
    }
}
