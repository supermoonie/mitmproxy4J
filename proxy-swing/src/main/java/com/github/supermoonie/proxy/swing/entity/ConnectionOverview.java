package com.github.supermoonie.proxy.swing.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
@DatabaseTable(tableName = "connection_overview")
public class ConnectionOverview {

    public static final String REQUEST_ID_FIELD_NAME = "request_id";
    public static final String CLIENT_HOST_FIELD_NAME = "client_host";
    public static final String CLIENT_PORT_FIELD_NAME = "client_port";
    public static final String CLIENT_SESSION_ID_FIELD_NAME = "client_session_id";
    public static final String CLIENT_PROTOCOL_FIELD_NAME = "client_protocol";
    public static final String CLIENT_CIPHER_SUITE_FIELD_NAME = "client_cipher_suite";
    public static final String REMOTE_IP_LIST_FIELD_NAME = "remote_ip_list";
    public static final String SELECTED_REMOTE_IP_FIELD_NAME = "selected_remote_ip";
    public static final String SERVER_SESSION_ID_FIELD_NAME = "server_session_id";
    public static final String SERVER_PROTOCOL_FIELD_NAME = "server_protocol";
    public static final String SERVER_CIPHER_SUITE_FIELD_NAME = "server_cipher_suite";
    public static final String USE_SECOND_PROXY_FIELD_NAME = "use_second_proxy";
    public static final String SECOND_PROXY_HOST_FIELD_NAME = "second_proxy_host";
    public static final String SECOND_PROXY_PORT_FIELD_NAME = "second_proxy_port";
    public static final String CONNECT_START_TIME_FIELD_NAME = "connect_start_time";
    public static final String CONNECT_END_TIME_FIELD_NAME = "connect_end_time";
    public static final String DNS_START_TIME_FIELD_NAME = "dns_start_time";
    public static final String DNS_END_TIME_FIELD_NAME = "dns_end_time";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = REQUEST_ID_FIELD_NAME, canBeNull = false, uniqueIndex = true, uniqueIndexName = "uk_request_id")
    private Integer requestId;
    @DatabaseField(columnName = CLIENT_HOST_FIELD_NAME)
    private String clientHost;
    @DatabaseField(columnName = CLIENT_PORT_FIELD_NAME)
    private Integer clientPort;
    @DatabaseField(columnName = CLIENT_SESSION_ID_FIELD_NAME)
    private String clientSessionId;
    @DatabaseField(columnName = CLIENT_PROTOCOL_FIELD_NAME)
    private String clientProtocol;
    @DatabaseField(columnName = CLIENT_CIPHER_SUITE_FIELD_NAME)
    private String clientCipherSuite;
    @DatabaseField(columnName = REMOTE_IP_LIST_FIELD_NAME)
    private String remoteIpList;
    @DatabaseField(columnName = SELECTED_REMOTE_IP_FIELD_NAME)
    private String selectedRemoteIp;
    @DatabaseField(columnName = SERVER_SESSION_ID_FIELD_NAME)
    private String serverSessionId;
    @DatabaseField(columnName = SERVER_PROTOCOL_FIELD_NAME)
    private String serverProtocol;
    @DatabaseField(columnName = SERVER_CIPHER_SUITE_FIELD_NAME)
    private String serverCipherSuite;
    @DatabaseField(columnName = USE_SECOND_PROXY_FIELD_NAME)
    private Integer useSecondProxy;
    @DatabaseField(columnName = SECOND_PROXY_HOST_FIELD_NAME)
    private String secondProxyHost;
    @DatabaseField(columnName = SECOND_PROXY_PORT_FIELD_NAME)
    private Integer secondProxyPort;
    @DatabaseField(columnName = CONNECT_START_TIME_FIELD_NAME)
    private Long connectStartTime;
    @DatabaseField(columnName = CONNECT_END_TIME_FIELD_NAME)
    private Long connectEndTime;
    @DatabaseField(columnName = DNS_START_TIME_FIELD_NAME)
    private Long dnsStartTime;
    @DatabaseField(columnName = DNS_END_TIME_FIELD_NAME)
    private Long dnsEndTime;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
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

    public String getRemoteIpList() {
        return remoteIpList;
    }

    public void setRemoteIpList(String remoteIpList) {
        this.remoteIpList = remoteIpList;
    }

    public String getSelectedRemoteIp() {
        return selectedRemoteIp;
    }

    public void setSelectedRemoteIp(String selectedRemoteIp) {
        this.selectedRemoteIp = selectedRemoteIp;
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

    public Long getConnectStartTime() {
        return connectStartTime;
    }

    public void setConnectStartTime(Long connectStartTime) {
        this.connectStartTime = connectStartTime;
    }

    public Long getConnectEndTime() {
        return connectEndTime;
    }

    public void setConnectEndTime(Long connectEndTime) {
        this.connectEndTime = connectEndTime;
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "ConnectionOverview{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", clientHost='" + clientHost + '\'' +
                ", clientPort=" + clientPort +
                ", clientSessionId='" + clientSessionId + '\'' +
                ", clientProtocol='" + clientProtocol + '\'' +
                ", clientCipherSuite='" + clientCipherSuite + '\'' +
                ", remoteIpList='" + remoteIpList + '\'' +
                ", selectedRemoteIp='" + selectedRemoteIp + '\'' +
                ", serverSessionId='" + serverSessionId + '\'' +
                ", serverProtocol='" + serverProtocol + '\'' +
                ", serverCipherSuite='" + serverCipherSuite + '\'' +
                ", useSecondProxy=" + useSecondProxy +
                ", secondProxyHost='" + secondProxyHost + '\'' +
                ", secondProxyPort=" + secondProxyPort +
                ", connectStartTime=" + connectStartTime +
                ", connectEndTime=" + connectEndTime +
                ", dnsStartTime=" + dnsStartTime +
                ", dnsEndTime=" + dnsEndTime +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
