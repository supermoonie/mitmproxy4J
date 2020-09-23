package com.github.supermoonie.proxy.platform.mac;

import com.github.supermoonie.proxy.platform.mac.info.HardwarePortInfo;
import com.github.supermoonie.proxy.platform.mac.info.NetworkServiceInfo;
import com.github.supermoonie.proxy.platform.mac.info.NetworkServiceOrderItem;
import com.github.supermoonie.proxy.platform.mac.info.ProxyInfo;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/9/21
 */
public class NetworkSetupTest {

    @Test
    public void listAllNetworkServices() throws IOException {
        List<String> services = NetworkSetup.listAllNetworkServices();
        System.out.println(services);
    }

    @Test
    public void listNetworkServiceOrder() throws IOException {
        List<NetworkServiceOrderItem> orderItems = NetworkSetup.listNetworkServiceOrder();
        System.out.println(orderItems.toString());
    }

    @Test
    public void getInfo() throws IOException {
        NetworkServiceInfo info = NetworkSetup.getInfo("WI-FI");
        System.out.println(info);
    }

    @Test
    public void listAllHardwarePorts() throws IOException {
        List<HardwarePortInfo> infoList = NetworkSetup.listAllHardwarePorts();
        System.out.println(infoList);
    }

    @Test
    public void getMacAddress() throws IOException {
        String macAddress = NetworkSetup.getMacAddress("Wi-Fi");
        System.out.println(macAddress);
    }

    @Test
    public void getWebProxy() throws IOException {
        ProxyInfo webProxy = NetworkSetup.getWebProxy("Wi-Fi");
        System.out.println(webProxy);
    }

    @Test
    public void setWebProxy() throws IOException {
        ProxyInfo proxyInfo = NetworkSetup.setWebProxy("Wi-Fi", "127.0.0.1", 7890, true, "foo", "bar");
        System.out.println(proxyInfo);
    }

    @Test
    public void setWebProxyState() throws IOException {
        NetworkSetup.setWebProxyState("Wi-Fi", true);
    }

    @Test
    public void getSecureWebProxy() throws IOException {
        ProxyInfo webProxy = NetworkSetup.getSecureWebProxy("Wi-Fi");
        System.out.println(webProxy);
    }

    @Test
    public void setSecureWebProxy() throws IOException {
        ProxyInfo proxyInfo = NetworkSetup.setSecureWebProxy("Wi-Fi", "127.0.0.1", 7890, true, "foo", "bar");
        System.out.println(proxyInfo);
    }

    @Test
    public void setSecureWebProxyState() throws IOException {
        NetworkSetup.setSecureWebProxyState("Wi-Fi", true);
    }

    @Test
    public void getSocksFirewallProxy() throws IOException {
        ProxyInfo proxyInfo = NetworkSetup.getSocksFirewallProxy("wi-fi");
        System.out.println(proxyInfo);
    }

    @Test
    public void setSocksFirewallProxy() throws IOException {
        ProxyInfo proxyInfo = NetworkSetup.setSocksFirewallProxy("Wi-Fi", "127.0.0.1", 7891, true, "foo", "bar");
        System.out.println(proxyInfo);
    }

    @Test
    public void setSocksFirewallProxyState() throws IOException {
        NetworkSetup.setSocksFirewallProxyState("wi-fi", true);
    }

    @Test
    public void getProxyBypassDomains() throws IOException {
        List<String> proxyBypassDomains = NetworkSetup.getProxyBypassDomains("Wi-Fi");
        System.out.println(proxyBypassDomains);
    }

    @Test
    public void setProxyBypassDomains() throws IOException {
        List<String> domains = NetworkSetup.setProxyBypassDomains("Wi-Fi", "192.168.0.0/16", "10.0.0.0/8", "172.16.0.0/12", "127.0.0.1", "localhost", "*.local", "timestamp.apple.com", "192.168.1.80");
        System.out.println(domains);
    }

    @Test
    public void setDnsServers() throws IOException {
        List<String> dnsServers = NetworkSetup.setDnsServers("wi-fi", "8.8.8.8");
        System.out.println(dnsServers);
    }

    @Test
    public void getDnsServers() throws IOException {
        List<String> dnsServers = NetworkSetup.getDnsServers("wi-fi");
        System.out.println(dnsServers);
    }

    @Test
    public void listPreferredWirelessNetworks() throws IOException {
        List<String> list = NetworkSetup.listPreferredWirelessNetworks("wi-fi");
        System.out.println(list);
    }
}