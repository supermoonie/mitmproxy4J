package com.github.supermoonie.proxy.fx.ui;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author supermoonie
 * @date 2021-03-21
 */
public class FormData {

    private final Property<String> key = new SimpleStringProperty();

    private final Property<String> value = new SimpleStringProperty();

    private final Property<String> contentType = new SimpleStringProperty();

    public FormData() {
    }

    public FormData(String key, String value, String contentType) {
        this.key.setValue(key);
        this.value.setValue(value);
        this.contentType.setValue(contentType);
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

    public String getContentType() {
        return contentType.getValue();
    }

    public Property<String> contentTypeProperty() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType.setValue(contentType);
    }
}
