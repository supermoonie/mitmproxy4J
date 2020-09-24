package com.github.supermoonie.proxy.platform;

import com.github.supermoonie.proxy.platform.mac.NetworkSetup;
import com.github.supermoonie.proxy.platform.mac.info.ProxyInfo;
import com.github.supermoonie.proxy.platform.win.WinInet;
import io.netty.util.internal.PlatformDependent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * @author supermoonie
 * @since 2020/9/23
 */
public class ProxySetup {

    private ProxySetup() {
    }

    public static boolean isEnable(String host, int port) throws IOException {
        if (PlatformDependent.isWindows()) {
            // TODO
        } else if (PlatformDependent.isOsx()) {
            List<String> allNetworkServices = NetworkSetup.listAllNetworkServices();
            for (String networkService : allNetworkServices) {
                if (networkService.toLowerCase().contains("wi-fi") || networkService.toLowerCase().contains("lan")) {
                    ProxyInfo webProxy = NetworkSetup.getWebProxy(networkService);
                    ProxyInfo secureWebProxy = NetworkSetup.getSecureWebProxy(networkService);
                    ProxyInfo socksFirewallProxy = NetworkSetup.getSocksFirewallProxy(networkService);
                    if (null == webProxy.getServer() || null == secureWebProxy.getServer() || null == socksFirewallProxy.getServer()) {
                        return false;
                    }
                    boolean flag = webProxy.getServer().equals(host) && webProxy.getPort() == port
                            && secureWebProxy.getServer().equals(host) && secureWebProxy.getPort() == port
                            && socksFirewallProxy.getServer().equals(host) && socksFirewallProxy.getPort() == port;
                    if (flag) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void enableHttpProxy(String host, int port, String username, String password) throws IOException {
        if (PlatformDependent.isWindows()) {
            // TODO
//            String interName = getRemoteInterface();
//            WinInet.INTERNET_PER_CONN_OPTION_LIST list = buildOptionList(interName, 2);
//            WinInet.INTERNET_PER_CONN_OPTION[] pOptions = (WinInet.INTERNET_PER_CONN_OPTION[]) list.pOptions
//                    .toArray(list.dwOptionCount);
//
//            // Set flags.
//            pOptions[0].dwOption = WinInet.INTERNET_PER_CONN_FLAGS;
//            pOptions[0].Value.dwValue = WinInet.PROXY_TYPE_PROXY;
//            pOptions[0].Value.setType(int.class);
//
//            // Set proxy name.
//            pOptions[1].dwOption = WinInet.INTERNET_PER_CONN_PROXY_SERVER;
//            pOptions[1].Value.pszValue = host + ":" + port;
//            pOptions[1].Value.setType(String.class);
//
//            refreshOptions(list);
        } else if (PlatformDependent.isOsx()) {
            List<String> allNetworkServices = NetworkSetup.listAllNetworkServices();
            boolean authenticated = null != username && !"".equals(username) && null != password && !"".equals(password);
            for (String networkService : allNetworkServices) {
                if (networkService.toLowerCase().contains("wi-fi") || networkService.toLowerCase().contains("lan")) {
                    NetworkSetup.setWebProxy(networkService, host, port, authenticated, username, password);
                    NetworkSetup.setSecureWebProxy(networkService, host, port, authenticated, username, password);
                    NetworkSetup.setSocksFirewallProxy(networkService, host, port, authenticated, username, password);
                }
            }
        }
    }

    public static void enableSocksProxy(String host, int port, String username, String password) throws IOException {
        if (PlatformDependent.isWindows()) {
            // TODO
//            String interName = getRemoteInterface();
//            WinInet.INTERNET_PER_CONN_OPTION_LIST list = buildOptionList(interName, 2);
//            WinInet.INTERNET_PER_CONN_OPTION[] pOptions = (WinInet.INTERNET_PER_CONN_OPTION[]) list.pOptions
//                    .toArray(list.dwOptionCount);
//
//            // Set flags.
//            pOptions[0].dwOption = WinInet.INTERNET_PER_CONN_FLAGS;
//            pOptions[0].Value.dwValue = WinInet.PROXY_TYPE_PROXY;
//            pOptions[0].Value.setType(int.class);
//
//            // Set proxy name.
//            pOptions[1].dwOption = WinInet.INTERNET_PER_CONN_PROXY_SERVER;
//            pOptions[1].Value.pszValue = host + ":" + port;
//            pOptions[1].Value.setType(String.class);
//
//            refreshOptions(list);
        } else if (PlatformDependent.isOsx()) {
            List<String> allNetworkServices = NetworkSetup.listAllNetworkServices();
            boolean authenticated = null != username && !"".equals(username) && null != password && !"".equals(password);
            for (String networkService : allNetworkServices) {
                if (networkService.toLowerCase().contains("wi-fi") || networkService.toLowerCase().contains("lan")) {
                    NetworkSetup.setSocksFirewallProxy(networkService, host, port, authenticated, username, password);
                }
            }
        }
    }

    public static void disableSocksProxy() throws IOException {
        if (PlatformDependent.isWindows()) {
            // TODO
        } else if (PlatformDependent.isOsx()) {
            List<String> allNetworkServices = NetworkSetup.listAllNetworkServices();
            for (String networkService : allNetworkServices) {
                if (networkService.toLowerCase().contains("wi-fi") || networkService.toLowerCase().contains("lan")) {
                    NetworkSetup.setSocksFirewallProxyState(networkService, false);
                }
            }
        }
    }

    public static void disableHttpProxy() throws IOException {
        if (PlatformDependent.isWindows()) {
            // TODO
        } else if (PlatformDependent.isOsx()) {
            List<String> allNetworkServices = NetworkSetup.listAllNetworkServices();
            for (String networkService : allNetworkServices) {
                if (networkService.toLowerCase().contains("wi-fi") || networkService.toLowerCase().contains("lan")) {
                    NetworkSetup.setWebProxyState(networkService, false);
                    NetworkSetup.setSecureWebProxyState(networkService, false);
                    NetworkSetup.setSocksFirewallProxyState(networkService, false);
                }
            }
        }
    }

    /**
     * 获取本机所有网卡
     */
    public static Map<String, List<String>> getInterfacesInfo() throws SocketException {
        Map<String, List<String>> interfacesInfo = new HashMap<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress nextElement = addresses.nextElement();
                String name = networkInterface.getDisplayName();
                List<String> ipList = interfacesInfo.computeIfAbsent(name, k -> new ArrayList<>());
                ipList.add(nextElement.getHostAddress());
            }
        }
        return interfacesInfo;
    }

}
