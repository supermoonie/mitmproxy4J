package com.github.supermoonie.proxy.platform.mac;

/**
 * @author supermoonie
 * @since 2020/9/21
 */
public class NetworkServiceInfo {

    private ConfigType type;

    private String ip4;

    private String mask;

    private String ip4Router;

    private String clientId;

    private String ip6;

    private String ip6Router;

    private String mac;

    public static enum ConfigType{
        /**
         * config type
         */
        DHCP("DHCP"),
        STATIC("STATIC")
        ;
        private final String value;

        ConfigType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }

    public String getIp4() {
        return ip4;
    }

    public void setIp4(String ip4) {
        this.ip4 = ip4;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getIp4Router() {
        return ip4Router;
    }

    public void setIp4Router(String ip4Router) {
        this.ip4Router = ip4Router;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIp6() {
        return ip6;
    }

    public void setIp6(String ip6) {
        this.ip6 = ip6;
    }

    public String getIp6Router() {
        return ip6Router;
    }

    public void setIp6Router(String ip6Router) {
        this.ip6Router = ip6Router;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "NetworkServiceInfo{" +
                "type=" + type +
                ", ip4='" + ip4 + '\'' +
                ", mask='" + mask + '\'' +
                ", ip4Router='" + ip4Router + '\'' +
                ", clientId='" + clientId + '\'' +
                ", ip6='" + ip6 + '\'' +
                ", ip6Router='" + ip6Router + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
