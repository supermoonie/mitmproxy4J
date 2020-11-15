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

    private String hostHeader;

    private String remoteHost;

    private int remotePort;

    private String clientHost;

    private int clientPort;

    private String dnsServer;

    private List<InetAddress> remoteAddressList;

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

    public String getDnsServer() {
        return dnsServer;
    }

    public void setDnsServer(String dnsServer) {
        this.dnsServer = dnsServer;
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
                ", clientProtocol='" + clientProtocol + '\'' +
                ", clientCipherSuite='" + clientCipherSuite + '\'' +
                ", clientSessionId='" + clientSessionId + '\'' +
                ", localCertificates=" + localCertificates +
                ", serverSessionId='" + serverSessionId + '\'' +
                ", serverProtocol='" + serverProtocol + '\'' +
                ", serverCipherSuite='" + serverCipherSuite + '\'' +
                ", serverCertificates=" + serverCertificates +
                ", useSecondProxy=" + useSecondProxy +
                ", secondProxyHost='" + secondProxyHost + '\'' +
                ", secondProxyPort=" + secondProxyPort +
                ", finished=" + finished +
                '}';
    }
}
