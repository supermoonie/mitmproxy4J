package com.github.supermoonie.proxy.fx.setting;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class GlobalSetting {

    private static GlobalSetting instance = new GlobalSetting();

    private GlobalSetting() {}

    private final SimpleBooleanProperty record = new SimpleBooleanProperty(true);

    private final SimpleIntegerProperty port = new SimpleIntegerProperty(10801);

    private final SimpleBooleanProperty throttling = new SimpleBooleanProperty(false);

    private final SimpleLongProperty throttlingWriteLimit = new SimpleLongProperty(32 * 1024);

    private final SimpleLongProperty throttlingReadLimit = new SimpleLongProperty(64 * 1024);

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
}
