package com.github.supermoonie.constant;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author supermoonie
 * @since 2020/7/15
 */
public enum EnumBodyType {

    /**
     * none
     */
    NONE("none"),

    FORM_DATA("formData"),

    X_WWW_FORM_URLENCODED("x-www-form-urlencoded"),

    BINARY("binary"),

    JSON("JSON"),

    TEXT("Text"),

    JAVASCRIPT("JavaScript"),

    HTML("HTML"),

    XML("XML");

    private final String name;

    EnumBodyType(String name) {
        this.name = name;
    }

    public static Optional<EnumBodyType> of(String name) {
        return Arrays.stream(EnumBodyType.values()).filter(type -> type.name.equals(name)).findFirst();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
