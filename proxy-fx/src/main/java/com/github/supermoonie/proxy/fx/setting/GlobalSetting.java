package com.github.supermoonie.proxy.fx.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.supermoonie.proxy.fx.support.AllowUrl;
import com.github.supermoonie.proxy.fx.support.BlockUrl;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class GlobalSetting {

    private static GlobalSetting instance = new GlobalSetting();

    private GlobalSetting() {
    }

    private final SimpleBooleanProperty record = new SimpleBooleanProperty(true);

    private final SimpleIntegerProperty port = new SimpleIntegerProperty(10801);

    private final SimpleBooleanProperty throttling = new SimpleBooleanProperty(true);

    private final SimpleLongProperty throttlingWriteLimit = new SimpleLongProperty(320 * 1024);

    private final SimpleLongProperty throttlingReadLimit = new SimpleLongProperty(640 * 1024);

    private final SimpleBooleanProperty blockUrl = new SimpleBooleanProperty(false);

    private final SimpleListProperty<BlockUrl> blockUrlList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final SimpleBooleanProperty allowUrl = new SimpleBooleanProperty(false);

    private final SimpleListProperty<AllowUrl> allowUrlList = new SimpleListProperty<>(FXCollections.observableArrayList());

    public static GlobalSetting getInstance() {
        return instance;
    }

    public synchronized static void setInstance(GlobalSetting instance) {
        GlobalSetting.instance = instance;
    }

    public boolean isRecord() {
        return record.get();
    }

    public SimpleBooleanProperty recordProperty() {
        return record;
    }

    @JsonProperty
    public void setRecord(boolean record) {
        this.record.set(record);
    }

    public int getPort() {
        return port.get();
    }

    public SimpleIntegerProperty portProperty() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port.set(port);
    }

    public boolean isThrottling() {
        return throttling.get();
    }

    public SimpleBooleanProperty throttlingProperty() {
        return throttling;
    }

    @JsonProperty
    public void setThrottling(boolean throttling) {
        this.throttling.set(throttling);
    }

    public long getThrottlingWriteLimit() {
        return throttlingWriteLimit.get();
    }

    public SimpleLongProperty throttlingWriteLimitProperty() {
        return throttlingWriteLimit;
    }

    @JsonProperty
    public void setThrottlingWriteLimit(long throttlingWriteLimit) {
        this.throttlingWriteLimit.set(throttlingWriteLimit);
    }

    public long getThrottlingReadLimit() {
        return throttlingReadLimit.get();
    }

    public SimpleLongProperty throttlingReadLimitProperty() {
        return throttlingReadLimit;
    }

    @JsonProperty
    public void setThrottlingReadLimit(long throttlingReadLimit) {
        this.throttlingReadLimit.set(throttlingReadLimit);
    }

    public boolean isBlockUrl() {
        return blockUrl.get();
    }

    public SimpleBooleanProperty blockUrlProperty() {
        return blockUrl;
    }

    @JsonProperty
    public void setBlockUrl(boolean blockUrl) {
        this.blockUrl.set(blockUrl);
    }

    public ObservableList<BlockUrl> getBlockUrlList() {
        return blockUrlList.get();
    }

    public SimpleListProperty<BlockUrl> blockUrlListProperty() {
        return blockUrlList;
    }

    @JsonProperty
    public void setBlockUrlList(ObservableList<BlockUrl> blockUrlList) {
        this.blockUrlList.set(blockUrlList);
    }

    public boolean isAllowUrl() {
        return allowUrl.get();
    }

    public SimpleBooleanProperty allowUrlProperty() {
        return allowUrl;
    }

    @JsonProperty
    public void setAllowUrl(boolean allowUrl) {
        this.allowUrl.set(allowUrl);
    }

    public ObservableList<AllowUrl> getAllowUrlList() {
        return allowUrlList.get();
    }

    public SimpleListProperty<AllowUrl> allowUrlListProperty() {
        return allowUrlList;
    }

    @JsonProperty
    public void setAllowUrlList(ObservableList<AllowUrl> allowUrlList) {
        this.allowUrlList.set(allowUrlList);
    }
}
