package com.github.supermoonie.proxy.fx.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;

/**
 * @author supermoonie
 * @since 2020/10/19
 */
public class FormDataColumnMap {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty value = new SimpleStringProperty();
    private final StringProperty valueType = new SimpleStringProperty();
    private final StringProperty contentType = new SimpleStringProperty("Auto");
    private final StringProperty fileName = new SimpleStringProperty();
    private File file;
    private String hexFile;

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getValueType() {
        return valueType.get();
    }

    public StringProperty valueTypeProperty() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType.set(valueType);
    }

    public String getContentType() {
        return contentType.get();
    }

    public StringProperty contentTypeProperty() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType.set(contentType);
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getHexFile() {
        return hexFile;
    }

    public void setHexFile(String hexFile) {
        this.hexFile = hexFile;
    }
}
