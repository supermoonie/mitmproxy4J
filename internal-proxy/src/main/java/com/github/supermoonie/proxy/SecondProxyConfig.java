package com.github.supermoonie.proxy;

/**
 * @author supermoonie
 * @since 2021/1/26
 */
public class SecondProxyConfig {

    private ProxyType proxyType;
    private String host;
    private int port;
    private String username;
    private String password;

    public SecondProxyConfig() {
    }

    public SecondProxyConfig(ProxyType proxyType, String host, int port) {
        this.proxyType = proxyType;
        this.host = host;
        this.port = port;
    }

    public SecondProxyConfig(ProxyType proxyType, String host, int port, String username, String password) {
        this.proxyType = proxyType;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public ProxyType getProxyType() {
        return proxyType;
    }

    public void setProxyType(ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SecondProxyConfig{" +
                "proxyType=" + proxyType +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
