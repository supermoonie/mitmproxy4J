package com.github.supermoonie.proxy.fx.entity;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
public class Header extends BaseEntity {

    private String name;

    private String value;

    private String requestId;

    private String responseId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    @Override
    public String toString() {
        return "Header{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", requestId='" + requestId + '\'' +
                ", responseId='" + responseId + '\'' +
                '}';
    }
}
