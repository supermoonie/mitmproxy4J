package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.dns.ConfigurableNameResolver;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class DnsProxyTest {

    public static void main(String[] args) throws UnknownHostException {
        InternalProxy proxy = new InternalProxy();
        Map<String, InetAddress> dnsMap = ConfigurableNameResolver.getDnsMap();
        dnsMap.put("github.com", Inet4Address.getByName("52.74.223.119"));
        proxy.setPort(10801);
        proxy.start();
    }
}
