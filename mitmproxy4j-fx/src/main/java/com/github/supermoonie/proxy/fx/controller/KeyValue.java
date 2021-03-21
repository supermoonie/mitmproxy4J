package com.github.supermoonie.proxy.fx.controller;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class KeyValue {

    private final Property<String> key = new SimpleStringProperty();

    private final Property<String> value = new SimpleStringProperty();

    public KeyValue() {
    }

    public KeyValue(String key, String value) {
        this.key.setValue(key);
        this.value.setValue(value);
    }

    public String getKey() {
        return key.getValue();
    }

    public Property<String> keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.setValue(key);
    }

    public String getValue() {
        return value.getValue();
    }

    public Property<String> valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.setValue(value);
    }

}
