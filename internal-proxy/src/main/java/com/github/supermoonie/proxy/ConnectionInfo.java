package com.github.supermoonie.proxy;

import java.net.InetAddress;
import java.security.cert.Certificate;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/8/9
 */
public class ConnectionInfo {

    private String url;

    private String authorization;

    private String hostHeader;

    private String remoteHost;

    private int remotePort;

    private String clientHost;

    private int clientPort;

    private List<InetAddress> remoteAddressList;

    private InetAddress selectedRemoteAddress;

    private boolean isHttps = false;

    private String clientProtocol;

    private String clientCipherSuite;

    private String clientSessionId;

    private List<Certificate> localCertificates;

    private String serverSessionId;

    private String serverProtocol;

    private String serverCipherSuite;

    private List<Certificate> serverCertificates;

    private boolean useSecondProxy = false;

    private SecondProxyConfig secondProxyConfig;

    private long requestStartTime;

    private long requestEndTime;

    private long connectStartTime;

    private long connectEndTime;

    private long dnsStartTime;

    private long dnsEndTime;

    private long responseStartTime;

    private long responseEndTime;

    private int requestSize;

    private int responseSize;

    private int responseStatus;

    private volatile boolean finished = false;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
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

    public InetAddress getSelectedRemoteAddress() {
        return selectedRemoteAddress;
    }

    public void setSelectedRemoteAddress(InetAddress selectedRemoteAddress) {
        this.selectedRemoteAddress = selectedRemoteAddress;
    }

    public List<InetAddress> getRemoteAddressList() {
        return remoteAddressList;
    }

    public void setRemoteAddressList(List<InetAddress> remoteAddressList) {
        this.remoteAddressList = remoteAddressList;
    }

    public boolean isHttps() {
        return isHttps;
    }

    public void setHttps(boolean https) {
        isHttps = https;
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

    public List<Certificate> getServerCertificates() {
        return serverCertificates;
    }

    public void setServerCertificates(List<Certificate> serverCertificates) {
        this.serverCertificates = serverCertificates;
    }

    public String getClientSessionId() {
        return clientSessionId;
    }

    public void setClientSessionId(String clientSessionId) {
        this.clientSessionId = clientSessionId;
    }

    public List<Certificate> getLocalCertificates() {
        return localCertificates;
    }

    public void setLocalCertificates(List<Certificate> localCertificates) {
        this.localCertificates = localCertificates;
    }

    public String getServerSessionId() {
        return serverSessionId;
    }

    public void setServerSessionId(String serverSessionId) {
        this.serverSessionId = serverSessionId;
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

    public SecondProxyConfig getSecondProxyConfig() {
        return secondProxyConfig;
    }

    public void setSecondProxyConfig(SecondProxyConfig secondProxyConfig) {
        this.secondProxyConfig = secondProxyConfig;
    }

    public long getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public long getRequestEndTime() {
        return requestEndTime;
    }

    public void setRequestEndTime(long requestEndTime) {
        this.requestEndTime = requestEndTime;
    }

    public long getConnectStartTime() {
        return connectStartTime;
    }

    public void setConnectStartTime(long connectStartTime) {
        this.connectStartTime = connectStartTime;
    }

    public long getConnectEndTime() {
        return connectEndTime;
    }

    public void setConnectEndTime(long connectEndTime) {
        this.connectEndTime = connectEndTime;
    }

    public long getDnsStartTime() {
        return dnsStartTime;
    }

    public void setDnsStartTime(long dnsStartTime) {
        this.dnsStartTime = dnsStartTime;
    }

    public long getDnsEndTime() {
        return dnsEndTime;
    }

    public void setDnsEndTime(long dnsEndTime) {
        this.dnsEndTime = dnsEndTime;
    }

    public long getResponseStartTime() {
        return responseStartTime;
    }

    public void setResponseStartTime(long responseStartTime) {
        this.responseStartTime = responseStartTime;
    }

    public long getResponseEndTime() {
        return responseEndTime;
    }

    public void setResponseEndTime(long responseEndTime) {
        this.responseEndTime = responseEndTime;
    }

    public int getRequestSize() {
        return requestSize;
    }

    public void setRequestSize(int requestSize) {
        this.requestSize = requestSize;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
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
                ", authorization='" + authorization + '\'' +
                ", hostHeader='" + hostHeader + '\'' +
                ", remoteHost='" + remoteHost + '\'' +
                ", remotePort=" + remotePort +
                ", clientHost='" + clientHost + '\'' +
                ", clientPort=" + clientPort +
                ", remoteAddressList=" + remoteAddressList +
                ", isHttps=" + isHttps +
                ", clientProtocol='" + clientProtocol + '\'' +
                ", clientCipherSuite='" + clientCipherSuite + '\'' +
                ", clientSessionId='" + clientSessionId + '\'' +
                ", localCertificates=" + localCertificates +
                ", serverSessionId='" + serverSessionId + '\'' +
                ", serverProtocol='" + serverProtocol + '\'' +
                ", serverCipherSuite='" + serverCipherSuite + '\'' +
                ", serverCertificates=" + serverCertificates +
                ", useSecondProxy=" + useSecondProxy +
                ", requestStartTime=" + requestStartTime +
                ", requestEndTime=" + requestEndTime +
                ", connectStartTime=" + connectStartTime +
                ", connectEndTime=" + connectEndTime +
                ", dnsStartTime=" + dnsStartTime +
                ", dnsEndTime=" + dnsEndTime +
                ", responseStartTime=" + responseStartTime +
                ", responseEndTime=" + responseEndTime +
                ", requestSize=" + requestSize +
                ", responseSize=" + responseSize +
                ", responseStatus=" + responseStatus +
                ", finished=" + finished +
                '}';
    }
}
