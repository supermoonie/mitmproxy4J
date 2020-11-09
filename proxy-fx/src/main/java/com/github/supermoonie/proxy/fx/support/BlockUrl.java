package com.github.supermoonie.proxy.fx.support;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/11/5
 */
public class BlockUrl {

    private final SimpleBooleanProperty enable = new SimpleBooleanProperty(true);

    private final SimpleStringProperty urlRegex = new SimpleStringProperty();

    public BlockUrl() {
    }

    public BlockUrl(boolean enable, String urlRegex) {
        this.enable.set(enable);
        this.urlRegex.set(urlRegex);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockUrl blockUrl = (BlockUrl) o;
        return Objects.equals(urlRegex.get(), blockUrl.urlRegex.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(urlRegex.get());
    }
}
