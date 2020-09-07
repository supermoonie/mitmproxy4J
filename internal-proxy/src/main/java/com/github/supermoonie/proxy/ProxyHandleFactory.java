package com.github.supermoonie.proxy;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;

import java.net.InetSocketAddress;

/**
 * @author supermoonie
 */
public class ProxyHandleFactory {

    public static ProxyHandler build(InternalProxy.SecondProxyConfig config) {
        ProxyHandler proxyHandler = null;
        if (config != null) {
            boolean isAuth = config.getUsername() != null && config.getPassword() != null;
            InetSocketAddress inetSocketAddress = new InetSocketAddress(config.getHost(),
                    config.getPort());
            switch (config.getProxyType()) {
                case HTTP:
                    if (isAuth) {
                        proxyHandler = new HttpProxyHandler(inetSocketAddress,
                                config.getUsername(), config.getPassword());
                    } else {
                        proxyHandler = new HttpProxyHandler(inetSocketAddress);
                    }
                    break;
                case SOCKS4:
                    proxyHandler = new Socks4ProxyHandler(inetSocketAddress);
                    break;
                case SOCKS5:
                    if (isAuth) {
                        proxyHandler = new Socks5ProxyHandler(inetSocketAddress,
                                config.getUsername(), config.getPassword());
                    } else {
                        proxyHandler = new Socks5ProxyHandler(inetSocketAddress);
                    }
                    break;
                default:
                    break;
            }
        }
        return proxyHandler;

    }
}
