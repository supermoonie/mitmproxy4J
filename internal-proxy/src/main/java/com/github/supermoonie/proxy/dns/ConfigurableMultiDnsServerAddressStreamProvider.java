package com.github.supermoonie.proxy.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-11-14
 */
public class ConfigurableMultiDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {

    private final List<DnsServerAddressStreamProvider> providers;

    /**
     * Create a new instance.
     *
     * @param servers DNS servers.
     */
    public ConfigurableMultiDnsServerAddressStreamProvider(boolean useSystem, List<InetSocketAddress> servers) {
        this.providers = new LinkedList<>();
        if (useSystem) {
            this.providers.add(DnsServerAddressStreamProviders.platformDefault());
        }
        for (InetSocketAddress address : servers) {
            this.providers.add(new InternalSingletonDnsServerAddressStreamProvider(address));
        }
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
