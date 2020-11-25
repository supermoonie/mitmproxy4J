package com.github.supermoonie.proxy.swing.setting;

import com.github.supermoonie.proxy.swing.support.AllowUrl;
import com.github.supermoonie.proxy.swing.support.BlockUrl;
import com.github.supermoonie.proxy.swing.util.Jackson;

import java.util.Set;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public class GlobalSetting {

    private static final GlobalSetting instance = new GlobalSetting();

    private GlobalSetting() {
    }

    private Boolean record = Boolean.TRUE;
    private Integer port = 10801;
    private Boolean auth = false;
    private String username;
    private String password;
    private Boolean systemProxy = false;
    private Boolean throttling = false;
    private Long throttlingWriteLimit;
    private Long throttlingReadLimit;
    private Boolean blockUrl = false;
    private Set<BlockUrl> blockUrlList;
    private Boolean allowUrl = false;
    private Set<AllowUrl> allowUrlList;

    public static GlobalSetting getInstance() {
        return instance;
    }

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

    @Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}
