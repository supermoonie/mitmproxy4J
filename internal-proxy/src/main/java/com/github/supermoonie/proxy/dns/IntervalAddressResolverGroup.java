package com.github.supermoonie.proxy.dns;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InternalProxy;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import io.netty.util.concurrent.EventExecutor;

import java.net.InetSocketAddress;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class IntervalAddressResolverGroup extends AddressResolverGroup<InetSocketAddress> {

    private final DnsNameResolver dnsNameResolver;

    private final ConnectionInfo connectionInfo;

    public IntervalAddressResolverGroup(EventLoop eventLoop, InternalProxy.DnsNameResolverConfig dnsNameResolverConfig, ConnectionInfo connectionInfo) {
        ConfigurableMultiDnsServerAddressStreamProvider dnsServerAddressStreamProvider =
                new ConfigurableMultiDnsServerAddressStreamProvider(dnsNameResolverConfig.isUseSystemDefault(), dnsNameResolverConfig.getDnsServerList());
        dnsNameResolver = new DnsNameResolverBuilder()
                .nameServerProvider(dnsServerAddressStreamProvider)
                .eventLoop(eventLoop)
                .channelFactory(NioDatagramChannel::new)
                .build();
        this.connectionInfo = connectionInfo;
    }

    @Override
    protected AddressResolver<InetSocketAddress> newResolver(EventExecutor executor) throws Exception {
        try {
            InternalCompositeNameResolver internalCompositeNameResolver =
                    new InternalCompositeNameResolver(executor, connectionInfo, new ConfigurableNameResolver(executor), dnsNameResolver);
            return new InetSocketAddressResolver(executor, internalCompositeNameResolver);
        } catch (Exception e) {
            return new ConfigurableNameResolver(executor).asAddressResolver();
        }

    }
}
