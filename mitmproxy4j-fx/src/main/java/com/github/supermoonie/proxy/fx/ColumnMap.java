package com.github.supermoonie.proxy.fx;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-10-13
 */
public class ColumnMap {

    private String name;

    private String value;

    private boolean editable = true;

    public ColumnMap() {

    }

    public ColumnMap(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static List<ColumnMap> listOf(String raw) {
        List<ColumnMap> list = new LinkedList<>();
        String[] params = raw.split("&");
        for (String param : params) {
            ColumnMap map = of(param);
            if (null != map) {
                list.add(map);
            }
        }
        return list;
    }

    public static ColumnMap of(String raw) {
        String[] form = raw.split("=");
        if (form.length == 1) {
            return new ColumnMap(form[0], "");
        } else if (form.length == 2) {
            return new ColumnMap(form[0], URLDecoder.decode(form[1], StandardCharsets.UTF_8));
        }
        return null;
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString() {
        return "ColumnMap{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", editable=" + editable +
                '}';
    }
}
