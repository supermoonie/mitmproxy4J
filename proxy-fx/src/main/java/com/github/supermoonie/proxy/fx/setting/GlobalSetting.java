package com.github.supermoonie.proxy.fx.setting;

import com.github.supermoonie.proxy.fx.support.AllowUrl;
import com.github.supermoonie.proxy.fx.support.BlockUrl;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

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

    public ObservableList<BlockUrl> getBlockUrlList() {
        return blockUrlList.get();
    }

    public SimpleListProperty<BlockUrl> blockUrlListProperty() {
        return blockUrlList;
    }

    public void setBlockUrlList(ObservableList<BlockUrl> blockUrlList) {
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

    public ObservableList<AllowUrl> getAllowUrlList() {
        return allowUrlList.get();
    }

    public SimpleListProperty<AllowUrl> allowUrlListProperty() {
        return allowUrlList;
    }

    public void setAllowUrlList(ObservableList<AllowUrl> allowUrlList) {
        this.allowUrlList.set(allowUrlList);
    }
}
