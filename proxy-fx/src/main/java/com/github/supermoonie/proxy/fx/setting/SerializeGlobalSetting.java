package com.github.supermoonie.proxy.fx.setting;

import com.github.supermoonie.proxy.fx.support.AllowUrl;
import com.github.supermoonie.proxy.fx.support.BlockUrl;

import java.util.Set;

/**
 * @author supermoonie
 * @since 2020/11/8
 */
public class SerializeGlobalSetting {

    private Boolean record;

    private Integer port;

    private Boolean auth;

    private String username;

    private String password;

    private Boolean systemProxy;

    private Boolean throttling;

    private Long throttlingWriteLimit;

    private Long throttlingReadLimit;

    private Boolean blockUrl;

    private Set<BlockUrl> blockUrlList;

    private Boolean allowUrl;

    private Set<AllowUrl> allowUrlList;

    public Boolean getRecord() {
        return record;
    }

    public void setRecord(Boolean record) {
        this.record = record;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
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

    public Boolean getSystemProxy() {
        return systemProxy;
    }

    public void setSystemProxy(Boolean systemProxy) {
        this.systemProxy = systemProxy;
    }

    public Boolean getThrottling() {
        return throttling;
    }

    public void setThrottling(Boolean throttling) {
        this.throttling = throttling;
    }

    public Long getThrottlingWriteLimit() {
        return throttlingWriteLimit;
    }

    public void setThrottlingWriteLimit(Long throttlingWriteLimit) {
        this.throttlingWriteLimit = throttlingWriteLimit;
    }

    public Long getThrottlingReadLimit() {
        return throttlingReadLimit;
    }

    public void setThrottlingReadLimit(Long throttlingReadLimit) {
        this.throttlingReadLimit = throttlingReadLimit;
    }

    public Boolean getBlockUrl() {
        return blockUrl;
    }

    public void setBlockUrl(Boolean blockUrl) {
        this.blockUrl = blockUrl;
    }

    public Set<BlockUrl> getBlockUrlList() {
        return blockUrlList;
    }

    public void setBlockUrlList(Set<BlockUrl> blockUrlList) {
        this.blockUrlList = blockUrlList;
    }

    public Boolean getAllowUrl() {
        return allowUrl;
    }

    public void setAllowUrl(Boolean allowUrl) {
        this.allowUrl = allowUrl;
    }

    public Set<AllowUrl> getAllowUrlList() {
        return allowUrlList;
    }

    public void setAllowUrlList(Set<AllowUrl> allowUrlList) {
        this.allowUrlList = allowUrlList;
    }
}
