package com.github.supermoonie.proxy.dns;

import io.netty.resolver.InetNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class ConfigurableNameResolver extends InetNameResolver {

    private static final Map<String, List<InetAddress>> DNS_MAP = new ConcurrentHashMap<>();

    public ConfigurableNameResolver(EventExecutor executor) {
        super(executor);
    }

    @Override
    protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
        List<InetAddress> inetAddresses = DNS_MAP.get(inetHost);
        if (null == inetAddresses || inetAddresses.size() == 0) {
            promise.setFailure(new UnknownHostException(inetHost));
        } else {
            promise.setSuccess(inetAddresses.get(0));
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        List<InetAddress> inetAddresses = DNS_MAP.get(inetHost);
        if (null == inetAddresses || inetAddresses.size() == 0) {
            promise.setFailure(new UnknownHostException(inetHost));
        } else {
            promise.setSuccess(inetAddresses);
        }
    }

    public static Map<String, List<InetAddress>> getDnsMap() {
        return DNS_MAP;
    }
}
