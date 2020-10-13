package com.github.supermoonie.proxy.fx.dto;

/**
 * @author supermoonie
 * @date 2020-10-13
 */
public class ColumnMap {

    private String name;

    private String value;

    public ColumnMap() {

    }

    public ColumnMap(String name, String value) {
        this.name = name;
        this.value = value;
    }

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

    @Override
    public String toString() {
        return "ColumnMap{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
