package com.github.supermoonie.proxy.platform.mac;

import com.github.supermoonie.proxy.platform.mac.info.HardwarePortInfo;
import com.github.supermoonie.proxy.platform.mac.info.NetworkServiceInfo;
import com.github.supermoonie.proxy.platform.mac.info.NetworkServiceOrderItem;
import com.github.supermoonie.proxy.platform.mac.info.ProxyInfo;
import com.github.supermoonie.proxy.util.ExecUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/9/21
 */
public final class NetworkSetup {

    private static final Pattern NETWORK_SERVICE_ORDER_PATTERN = Pattern.compile("^\\(Hardware Port: (?<name>.*), Device: (?<device>.*)\\)$");

    private static final Pattern IP_PATTERN = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");

    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile("([a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2}:[a-zA-Z0-9]{2})");

    private static final String NETWORK_SETUP_CMD = "networksetup";

    public static List<String> listAllNetworkServices() throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-listallnetworkservices");
        return Arrays.stream(result.split("\n"))
                .filter(line -> !line.startsWith("An asterisk")).collect(Collectors.toList());
    }

    public static List<NetworkServiceOrderItem> listNetworkServiceOrder() throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-listnetworkserviceorder");
        List<String> lines = Arrays.stream(result.split("\n"))
                .filter(line -> !line.startsWith("An asterisk")).collect(Collectors.toList());
        List<NetworkServiceOrderItem> list = new ArrayList<>();
        int order = 1;
        for (String line : lines) {
            Matcher matcher = NETWORK_SERVICE_ORDER_PATTERN.matcher(line);
            if (matcher.find()) {
                NetworkServiceOrderItem item = new NetworkServiceOrderItem();
                item.setOrder(order);
                item.setName(matcher.group("name"));
                item.setDevice(matcher.group("device"));
                list.add(item);
                order = order + 1;
            }
        }
        return list;
    }

    public static NetworkServiceInfo getInfo(String networkService) throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-getinfo", networkService);
        String[] lines = result.split("\n");
        NetworkServiceInfo info = new NetworkServiceInfo();
        for (String line : lines) {
            if (line.startsWith("DHCP")) {
                info.setType(NetworkServiceInfo.ConfigType.DHCP);
            } else if (line.startsWith("STATIC")) {
                info.setType(NetworkServiceInfo.ConfigType.STATIC);
            } else if (line.startsWith("IP address")) {
                Matcher matcher = IP_PATTERN.matcher(line);
                if (matcher.find()) {
                    info.setIp4(matcher.group(1));
                }
            } else if (line.startsWith("Subnet mask")) {
                Matcher matcher = IP_PATTERN.matcher(line);
                if (matcher.find()) {
                    info.setMask(matcher.group(1));
                }
            } else if (line.startsWith("Router")) {
                Matcher matcher = IP_PATTERN.matcher(line);
                if (matcher.find()) {
                    info.setIp4Router(matcher.group(1));
                }
            } else if (line.startsWith("Client ID")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    info.setClientId(arr[1]);
                }
            } else if (line.startsWith("IPv6 IP address")) {
                String[] arr = line.split(": ");
                if (arr.length > 1 && !arr[1].equals("none")) {
                    info.setClientId(arr[1]);
                }
            } else if (line.startsWith("IPv6 Router")) {
                String[] arr = line.split(": ");
                if (arr.length > 1 && !arr[1].equals("none")) {
                    info.setIp6Router(arr[1]);
                }
            } else if (line.startsWith("Wi-Fi ID")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    info.setMac(arr[1]);
                }
            }
        }
        return info;
    }

    public static List<HardwarePortInfo> listAllHardwarePorts() throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-listallhardwareports");
        String[] segments = result.split("\r?\n\r?\n");
        List<HardwarePortInfo> list = new ArrayList<>();
        for (String segment : segments) {
            if (!segment.trim().startsWith("Hardware Port")) {
                continue;
            }
            HardwarePortInfo info = new HardwarePortInfo();
            list.add(info);
            String[] lines = segment.split("\r?\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("Hardware Port")) {
                    String[] arr = line.split(": ");
                    if (arr.length > 1) {
                        info.setHardwarePort(arr[1]);
                    }
                } else if (line.startsWith("Device")) {
                    String[] arr = line.split(": ");
                    if (arr.length > 1) {
                        info.setDevice(arr[1]);
                    }
                } else if (line.startsWith("Ethernet Address")) {
                    String[] arr = line.split(": ");
                    if (arr.length > 1) {
                        info.setEthernetAddress(arr[1]);
                    }
                }
            }
        }
        return list;
    }

    public static void detectNewHardware() throws IOException {
        ExecUtils.execBlock(NETWORK_SETUP_CMD, "-detectnewhardware");
    }

    public static String getMacAddress(String hardwarePort) throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-getmacaddress", hardwarePort);
        Matcher matcher = MAC_ADDRESS_PATTERN.matcher(result);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static List<String> getDnsServers(String networkService) throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-getdnsservers", networkService);
        String[] lines = result.split("\r?\n");
        return List.of(lines);
    }

    public static List<String> setDnsServers(String networkService, String... dnsServers) throws IOException {
        String dnsServerArg = String.join(" ", dnsServers);
        ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setdnsservers", networkService, dnsServerArg);
        return getDnsServers(networkService);
    }

    public static ProxyInfo getWebProxy(String networkService) throws IOException {
        return getProxy(networkService, "-getwebproxy");
    }

    private static ProxyInfo getProxy(String networkService, String cmd) throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, cmd, networkService);
        String[] lines = result.split("\r?\n");
        ProxyInfo info = new ProxyInfo();
        for (String line : lines) {
            if (line.startsWith("Enabled")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    info.setEnabled(arr[1].equalsIgnoreCase("yes"));
                }
            } else if (line.startsWith("Server")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    info.setServer(arr[1]);
                }
            } else if (line.startsWith("Port")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    info.setPort(Integer.parseInt(arr[1]));
                }
            }
        }
        return info;
    }

    public static ProxyInfo setWebProxy(String networkService,
                                        String domain,
                                        int port,
                                        boolean authenticated,
                                        String username,
                                        String password) throws IOException {
        if (authenticated && null != username && null != password) {
            ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setwebproxy", networkService, domain, String.valueOf(port), "on", username, password);
        } else {
            ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setwebproxy", networkService, domain, String.valueOf(port), "off");
        }
        return getWebProxy(networkService);
    }

    public static void setWebProxyState(String networkService, boolean on) throws IOException {
        ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setwebproxystate", networkService, on ? "on" : "off");
    }

    public static ProxyInfo getSecureWebProxy(String networkService) throws IOException {
        return getProxy(networkService, "-getsecurewebproxy");
    }

    public static ProxyInfo setSecureWebProxy(String networkService,
                                              String domain,
                                              int port,
                                              boolean authenticated,
                                              String username,
                                              String password) throws IOException {
        if (authenticated && null != username && null != password) {
            ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setsecurewebproxy", networkService, domain, String.valueOf(port), "on", username, password);
        } else {
            ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setsecurewebproxy", networkService, domain, String.valueOf(port), "off");
        }

        return getSecureWebProxy(networkService);
    }

    public static void setSecureWebProxyState(String networkService, boolean on) throws IOException {
        ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setsecurewebproxystate", networkService, on ? "on" : "off");
    }

    public static ProxyInfo getSocksFirewallProxy(String networkService) throws IOException {
        return getProxy(networkService, "-getsocksfirewallproxy");
    }

    public static ProxyInfo setSocksFirewallProxy(String networkService,
                                                  String domain,
                                                  int port,
                                                  boolean authenticated,
                                                  String username,
                                                  String password) throws IOException {
        if (authenticated && null != username && null != password) {
            ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setsocksfirewallproxy", networkService, domain, String.valueOf(port), "on", username, password);
        } else {
            ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setsocksfirewallproxy", networkService, domain, String.valueOf(port), "off");
        }
        return getSocksFirewallProxy(networkService);
    }

    public static void setSocksFirewallProxyState(String networkService, boolean on) throws IOException {
        ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setsocksfirewallproxystate", networkService, on ? "on" : "off");
    }

    public static List<String> getProxyBypassDomains(String networkService) throws IOException {
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-getproxybypassdomains", networkService);
        String[] lines = result.split("\r?\n");
        return List.of(lines);
    }

    public static List<String> setProxyBypassDomains(String networkService, String... domains) throws IOException {
        String domainsCmd = String.join("、", domains);
        ExecUtils.execBlock(NETWORK_SETUP_CMD, "-setproxybypassdomains", networkService, domainsCmd);
        return getProxyBypassDomains(networkService);
    }

    public static List<String> listPreferredWirelessNetworks(String networkService) throws IOException {
        List<HardwarePortInfo> hardwarePortInfos = listAllHardwarePorts();
        HardwarePortInfo hardwarePortInfo = hardwarePortInfos.stream().filter(info -> info.getHardwarePort().equalsIgnoreCase(networkService)).findFirst().orElseThrow(() -> new IllegalArgumentException(networkService + " not found"));
        String result = ExecUtils.exec(NETWORK_SETUP_CMD, "-listpreferredwirelessnetworks", hardwarePortInfo.getDevice());
        String[] lines = result.split("\r?\n");
        return Arrays.stream(lines).map(String::trim).collect(Collectors.toList()).subList(1, lines.length);
    }

}
