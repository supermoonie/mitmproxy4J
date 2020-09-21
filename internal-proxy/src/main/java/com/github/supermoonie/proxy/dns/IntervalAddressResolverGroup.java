package com.github.supermoonie.proxy.dns;

import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.concurrent.EventExecutor;

import java.net.InetSocketAddress;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class IntervalAddressResolverGroup extends AddressResolverGroup<InetSocketAddress> {

    public static final IntervalAddressResolverGroup INSTANCE = new IntervalAddressResolverGroup();

    private IntervalAddressResolverGroup() {

    }

    @Override
    protected AddressResolver<InetSocketAddress> newResolver(EventExecutor executor) throws Exception {
        return new ConfigurableNameResolver(executor).asAddressResolver();
    }
}
