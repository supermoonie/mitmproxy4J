package com.github.supermoonie.proxy.fx.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @since 2021/1/25
 */
@DatabaseTable(tableName = "external_proxy")
public class ExternalProxy {

    public static final int DISABLE = 0;
    public static final int ENABLE = 1;

    public static final String HOST_FIELD_NAME = "host";
    public static final String PROXY_HOST_FIELD_NAME = "proxy_host";
    public static final String PROXY_PORT_FIELD_NAME = "proxy_port";
    public static final String PROXY_TYPE_FIELD_NAME = "proxy_type";
    public static final String PROXY_AUTH_FIELD_NAME = "proxy_auth";
    public static final String PROXY_USER_FIELD_NAME = "proxy_user";
    public static final String PROXY_PWD_FIELD_NAME = "proxy_pwd";
    public static final String ENABLE_FIELD_NAME = "enable";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = HOST_FIELD_NAME)
    private String host;
    @DatabaseField(columnName = PROXY_HOST_FIELD_NAME)
    private String proxyHost;
    @DatabaseField(columnName = PROXY_PORT_FIELD_NAME)
    private Integer proxyPort;
    @DatabaseField(columnName = PROXY_TYPE_FIELD_NAME)
    private Integer proxyType;
    @DatabaseField(columnName = PROXY_AUTH_FIELD_NAME)
    private Integer proxyAuth;
    @DatabaseField(columnName = PROXY_USER_FIELD_NAME)
    private String proxyUser;
    @DatabaseField(columnName = PROXY_PWD_FIELD_NAME)
    private String proxyPwd;
    @DatabaseField(columnName = ENABLE_FIELD_NAME)
    private Integer enable;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public Integer getProxyType() {
        return proxyType;
    }

    public void setProxyType(Integer proxyType) {
        this.proxyType = proxyType;
    }

    public Integer getProxyAuth() {
        return proxyAuth;
    }

    public void setProxyAuth(Integer proxyAuth) {
        this.proxyAuth = proxyAuth;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProxyPwd() {
        return proxyPwd;
    }

    public void setProxyPwd(String proxyPwd) {
        this.proxyPwd = proxyPwd;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "ExternalProxy{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", proxyHost='" + proxyHost + '\'' +
                ", proxyPort=" + proxyPort +
                ", proxyType=" + proxyType +
                ", proxyAuth=" + proxyAuth +
                ", proxyUser='" + proxyUser + '\'' +
                ", proxyPwd='" + proxyPwd + '\'' +
                ", enable=" + enable +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
