package com.github.supermoonie.proxy.platform.mac;

/**
 * @author supermoonie
 * @since 2020/9/21
 */
public class ProxyInfo {

    private boolean enabled;

    private String server;

    private int port;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ProxyInfo{" +
                "enabled=" + enabled +
                ", server='" + server + '\'' +
                ", port=" + port +
                '}';
    }
}
