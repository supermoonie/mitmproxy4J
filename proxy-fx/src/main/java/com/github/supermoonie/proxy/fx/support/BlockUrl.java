package com.github.supermoonie.proxy.fx.support;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author supermoonie
 * @since 2020/11/5
 */
public class BlockUrl {

    private final SimpleBooleanProperty enable = new SimpleBooleanProperty(true);

    private final SimpleStringProperty urlRegex = new SimpleStringProperty();

    public boolean isEnable() {
        return enable.get();
    }

    public SimpleBooleanProperty enableProperty() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable.set(enable);
    }

    public String getUrlRegex() {
        return urlRegex.get();
    }

    public SimpleStringProperty urlRegexProperty() {
        return urlRegex;
    }

    public void setUrlRegex(String urlRegex) {
        this.urlRegex.set(urlRegex);
    }
}
