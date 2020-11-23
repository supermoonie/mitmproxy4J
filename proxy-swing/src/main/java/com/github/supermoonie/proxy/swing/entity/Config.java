package com.github.supermoonie.proxy.swing.entity;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
public class Config extends BaseEntity {

    private String key;

    private String value;

    private Integer type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Config{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}
