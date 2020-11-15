package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.dns.ConfigurableNameResolver;
import io.netty.util.internal.SocketUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class DnsProxyTest {

    private static final int DNS_DEFAULT_PORT = 53;

    public static void main(String[] args) throws UnknownHostException {
        InternalProxy proxy = new InternalProxy();
        Map<String, List<InetAddress>> dnsMap = InternalProxy.memoryDnsMap();
//        dnsMap.put("httpbin.org", List.of(Inet4Address.getByName("52.6.34.179")));
        InternalProxy.DnsNameResolverConfig dnsNameResolverConfig = proxy.getDnsNameResolverConfig();
        dnsNameResolverConfig.setUseSystemDefault(true);
        List<InetSocketAddress> dnsServerList = dnsNameResolverConfig.getDnsServerList();
        dnsServerList.add(SocketUtils.socketAddress("8.8.8.8", DNS_DEFAULT_PORT));
        dnsServerList.add(SocketUtils.socketAddress("8.8.4.4", DNS_DEFAULT_PORT));
        dnsServerList.add(SocketUtils.socketAddress("114.114.114.114", DNS_DEFAULT_PORT));
        proxy.setPort(10801);
        proxy.start();
    }
}
