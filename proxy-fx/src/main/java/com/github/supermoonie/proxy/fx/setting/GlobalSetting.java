package com.github.supermoonie.proxy.fx.setting;

import com.github.supermoonie.proxy.fx.support.AllowUrl;
import com.github.supermoonie.proxy.fx.support.BlockUrl;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.HashSet;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class GlobalSetting {

    private static final GlobalSetting instance = new GlobalSetting();

    private GlobalSetting() {
    }

    private final SimpleBooleanProperty record = new SimpleBooleanProperty(true);

    private final SimpleBooleanProperty auth = new SimpleBooleanProperty(false);

    private final SimpleIntegerProperty port = new SimpleIntegerProperty(10801);

    private final SimpleStringProperty username = new SimpleStringProperty();

    private final SimpleStringProperty password = new SimpleStringProperty();

    private final SimpleBooleanProperty systemProxy = new SimpleBooleanProperty(false);

    private final SimpleBooleanProperty throttling = new SimpleBooleanProperty(true);

    private final SimpleLongProperty throttlingWriteLimit = new SimpleLongProperty(320 * 1024);

    private final SimpleLongProperty throttlingReadLimit = new SimpleLongProperty(640 * 1024);

    private final SimpleBooleanProperty blockUrl = new SimpleBooleanProperty(false);

    private final SimpleSetProperty<BlockUrl> blockUrlList = new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<>()));

    private final SimpleBooleanProperty allowUrl = new SimpleBooleanProperty(false);

    private final SimpleSetProperty<AllowUrl> allowUrlList = new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<>()));

    public static GlobalSetting getInstance() {
        return instance;
    }

    public boolean isRecord() {
        return record.get();
    }

    public SimpleBooleanProperty recordProperty() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record.set(record);
    }

    public int getPort() {
        return port.get();
    }

    public SimpleIntegerProperty portProperty() {
        return port;
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public boolean isAuth() {
        return auth.get();
    }

    public SimpleBooleanProperty authProperty() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth.set(auth);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public boolean isSystemProxy() {
        return systemProxy.get();
    }

    public SimpleBooleanProperty systemProxyProperty() {
        return systemProxy;
    }

    public void setSystemProxy(boolean systemProxy) {
        this.systemProxy.set(systemProxy);
    }

    public boolean isThrottling() {
        return throttling.get();
    }

    public SimpleBooleanProperty throttlingProperty() {
        return throttling;
    }

    public void setThrottling(boolean throttling) {
        this.throttling.set(throttling);
    }

    public long getThrottlingWriteLimit() {
        return throttlingWriteLimit.get();
    }

    public SimpleLongProperty throttlingWriteLimitProperty() {
        return throttlingWriteLimit;
    }

    public void setThrottlingWriteLimit(long throttlingWriteLimit) {
        this.throttlingWriteLimit.set(throttlingWriteLimit);
    }

    public long getThrottlingReadLimit() {
        return throttlingReadLimit.get();
    }

    public SimpleLongProperty throttlingReadLimitProperty() {
        return throttlingReadLimit;
    }

    public void setThrottlingReadLimit(long throttlingReadLimit) {
        this.throttlingReadLimit.set(throttlingReadLimit);
    }

    public boolean isBlockUrl() {
        return blockUrl.get();
    }

    public SimpleBooleanProperty blockUrlProperty() {
        return blockUrl;
    }

    public void setBlockUrl(boolean blockUrl) {
        this.blockUrl.set(blockUrl);
    }

    public ObservableSet<BlockUrl> getBlockUrlList() {
        return blockUrlList.get();
    }

    public SimpleSetProperty<BlockUrl> blockUrlListProperty() {
        return blockUrlList;
    }

    public void setBlockUrlList(ObservableSet<BlockUrl> blockUrlList) {
        this.blockUrlList.set(blockUrlList);
    }

    public boolean isAllowUrl() {
        return allowUrl.get();
    }

    public SimpleBooleanProperty allowUrlProperty() {
        return allowUrl;
    }

    public void setAllowUrl(boolean allowUrl) {
        this.allowUrl.set(allowUrl);
    }

    public ObservableSet<AllowUrl> getAllowUrlList() {
        return allowUrlList.get();
    }

    public SimpleSetProperty<AllowUrl> allowUrlListProperty() {
        return allowUrlList;
    }

    public void setAllowUrlList(ObservableSet<AllowUrl> allowUrlList) {
        this.allowUrlList.set(allowUrlList);
    }
}
