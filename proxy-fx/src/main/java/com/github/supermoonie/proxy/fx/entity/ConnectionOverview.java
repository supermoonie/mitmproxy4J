package com.github.supermoonie.proxy.fx.entity;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
public class ConnectionOverview extends BaseEntity {

    private String requestId;
    private String clientHost;
    private Integer clientPort;
    private String clientSessionId;
    private String clientProtocol;
    private String clientCipherSuite;
    private String dnsServer;
    private String remoteIp;
    private String serverSessionId;
    private String serverProtocol;
    private String serverCipherSuite;
    private Integer useSecondProxy;
    private String secondProxyHost;
    private Integer secondProxyPort;
    private Long dnsStartTime;
    private Long dnsEndTime;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public String getDnsServer() {
        return dnsServer;
    }

    public void setDnsServer(String dnsServer) {
        this.dnsServer = dnsServer;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getClientProtocol() {
        return clientProtocol;
    }

    public void setClientProtocol(String clientProtocol) {
        this.clientProtocol = clientProtocol;
    }

    public String getClientCipherSuite() {
        return clientCipherSuite;
    }

    public void setClientCipherSuite(String clientCipherSuite) {
        this.clientCipherSuite = clientCipherSuite;
    }

    public String getClientSessionId() {
        return clientSessionId;
    }

    public void setClientSessionId(String clientSessionId) {
        this.clientSessionId = clientSessionId;
    }

    public String getServerSessionId() {
        return serverSessionId;
    }

    public void setServerSessionId(String serverSessionId) {
        this.serverSessionId = serverSessionId;
    }

    public String getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public String getServerCipherSuite() {
        return serverCipherSuite;
    }

    public void setServerCipherSuite(String serverCipherSuite) {
        this.serverCipherSuite = serverCipherSuite;
    }

    public Integer getUseSecondProxy() {
        return useSecondProxy;
    }

    public void setUseSecondProxy(Integer useSecondProxy) {
        this.useSecondProxy = useSecondProxy;
    }

    public String getSecondProxyHost() {
        return secondProxyHost;
    }

    public void setSecondProxyHost(String secondProxyHost) {
        this.secondProxyHost = secondProxyHost;
    }

    public Integer getSecondProxyPort() {
        return secondProxyPort;
    }

    public void setSecondProxyPort(Integer secondProxyPort) {
        this.secondProxyPort = secondProxyPort;
    }

    public Long getDnsStartTime() {
        return dnsStartTime;
    }

    public void setDnsStartTime(Long dnsStartTime) {
        this.dnsStartTime = dnsStartTime;
    }

    public Long getDnsEndTime() {
        return dnsEndTime;
    }

    public void setDnsEndTime(Long dnsEndTime) {
        this.dnsEndTime = dnsEndTime;
    }

    @Override
    public String toString() {
        return "ConnectionOverview{" +
                "requestId='" + requestId + '\'' +
                ", clientHost='" + clientHost + '\'' +
                ", clientPort=" + clientPort +
                ", dnsServer='" + dnsServer + '\'' +
                ", remoteIp='" + remoteIp + '\'' +
                ", clientProtocol='" + clientProtocol + '\'' +
                ", clientCipherSuite='" + clientCipherSuite + '\'' +
                ", clientSessionId='" + clientSessionId + '\'' +
                ", serverSessionId='" + serverSessionId + '\'' +
                ", serverProtocol='" + serverProtocol + '\'' +
                ", serverCipherSuite='" + serverCipherSuite + '\'' +
                ", useSecondProxy=" + useSecondProxy +
                ", secondProxyHost='" + secondProxyHost + '\'' +
                ", secondProxyPort=" + secondProxyPort +
                ", dnsStartTime=" + dnsStartTime +
                ", dnsEndTime=" + dnsEndTime +
                '}';
    }
}
