package com.github.supermoonie.proxy.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import io.netty.resolver.dns.SingletonDnsServerAddressStreamProvider;
import io.netty.util.internal.SocketUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-11-14
 */
public class ConfigurableMultiDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {

    public static final int DNS_PORT = 53;

    public static final ConfigurableMultiDnsServerAddressStreamProvider INSTANCE
            = new ConfigurableMultiDnsServerAddressStreamProvider(new LinkedList<>());

    private final List<DnsServerAddressStreamProvider> providers;

    static {
        INSTANCE.getProviders().add(DnsServerAddressStreamProviders.platformDefault());
        INSTANCE.getProviders().add(new SingletonDnsServerAddressStreamProvider(SocketUtils.socketAddress("8.8.8.8", DNS_PORT)));
    }

    /**
     * Create a new instance.
     *
     * @param providers The providers to use for DNS resolution. They will be queried in order.
     */
    private ConfigurableMultiDnsServerAddressStreamProvider(List<DnsServerAddressStreamProvider> providers) {
        this.providers = providers;
    }

    @Override
    public DnsServerAddressStream nameServerAddressStream(String hostname) {
        for (DnsServerAddressStreamProvider provider : providers) {
            DnsServerAddressStream stream = provider.nameServerAddressStream(hostname);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

    public List<DnsServerAddressStreamProvider> getProviders() {
        return providers;
    }
}
