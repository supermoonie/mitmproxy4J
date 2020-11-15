package com.github.supermoonie.proxy.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddresses;

import java.net.InetSocketAddress;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
public class InternalSingletonDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {

    private final DnsServerAddresses addresses;
    private final InetSocketAddress address;

    public InternalSingletonDnsServerAddressStreamProvider(InetSocketAddress address) {
        this.address = address;
        this.addresses = DnsServerAddresses.singleton(address);
    }

    @Override
    public DnsServerAddressStream nameServerAddressStream(String hostname) {
        return addresses.stream();
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
