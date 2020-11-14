package com.github.supermoonie.proxy.dns;

import io.netty.resolver.InetNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class ConfigurableNameResolver extends InetNameResolver {

    private static final Map<String, InetAddress> DNS_MAP = new ConcurrentHashMap<>();

    public ConfigurableNameResolver(EventExecutor executor) {
        super(executor);
    }

    @Override
    protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
        try {
            InetAddress inetAddress = DNS_MAP.get(inetHost);
            if (null != inetAddress) {
                promise.setSuccess(inetAddress);
            }
            throw new UnknownHostException(inetHost);
        } catch (UnknownHostException e) {
            promise.setFailure(e);
        }
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        try {
            InetAddress inetAddress = DNS_MAP.get(inetHost);
            if (null != inetAddress) {
                promise.setSuccess(Collections.singletonList(inetAddress));
            }
            throw new UnknownHostException(inetHost);
        } catch (UnknownHostException e) {
            promise.setFailure(e);
        }
    }

    public static Map<String, InetAddress> getDnsMap() {
        return DNS_MAP;
    }
}
