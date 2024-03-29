package com.github.supermoonie.proxy.fx.ui.compose;

/**
 * @author supermoonie
 * @since 2021/3/21
 */
public class FormData {

    private String name;

    private String type;

    private String value;

    private byte[] fileContent;

    private String contentType;

    public FormData() {
    }

    public FormData(String name, String type, String value, byte[] fileContent, String contentType) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.fileContent = fileContent;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
