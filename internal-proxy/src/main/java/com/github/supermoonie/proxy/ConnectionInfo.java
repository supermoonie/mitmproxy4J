package com.github.supermoonie.proxy;

/**
 * @author supermoonie
 * @since 2020/8/9
 */
public class ConnectionInfo {

    private String url;

    private String hostHeader;

    private String remoteHost;

    private int remotePort;

    private String clientHost;

    private int clientPort;

    private boolean isHttps = false;

    private boolean useSecondProxy = true;

    private String secondProxyHost;

    private int secondProxyPort;

    private volatile boolean finished = false;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public boolean isHttps() {
        return isHttps;
    }

    public void setHttps(boolean https) {
        isHttps = https;
    }

    public String getHostHeader() {
        return hostHeader;
    }

    public void setHostHeader(String hostHeader) {
        this.hostHeader = hostHeader;
    }

    public boolean isUseSecondProxy() {
        return useSecondProxy;
    }

    public void setUseSecondProxy(boolean useSecondProxy) {
        this.useSecondProxy = useSecondProxy;
    }

    public String getSecondProxyHost() {
        return secondProxyHost;
    }

    public void setSecondProxyHost(String secondProxyHost) {
        this.secondProxyHost = secondProxyHost;
    }

    public int getSecondProxyPort() {
        return secondProxyPort;
    }

    public void setSecondProxyPort(int secondProxyPort) {
        this.secondProxyPort = secondProxyPort;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ConnectionInfo{" +
                "url='" + url + '\'' +
                ", hostHeader='" + hostHeader + '\'' +
                ", remoteHost='" + remoteHost + '\'' +
                ", remotePort=" + remotePort +
                ", clientHost='" + clientHost + '\'' +
                ", clientPort=" + clientPort +
                ", isHttps=" + isHttps +
                ", useSecondProxy=" + useSecondProxy +
                ", finished=" + finished +
                '}';
    }
}
