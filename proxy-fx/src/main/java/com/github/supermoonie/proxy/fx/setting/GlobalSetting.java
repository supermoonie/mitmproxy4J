package com.github.supermoonie.proxy.fx.setting;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class GlobalSetting {

    private static GlobalSetting instance = new GlobalSetting();

    private GlobalSetting() {}

    private final SimpleBooleanProperty record = new SimpleBooleanProperty(true);

    private final SimpleIntegerProperty port = new SimpleIntegerProperty(10801);

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
}
