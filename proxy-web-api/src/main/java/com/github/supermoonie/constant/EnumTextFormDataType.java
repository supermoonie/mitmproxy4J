package com.github.supermoonie.constant;

/**
 * @author supermoonie
 * @since 2020/7/15
 */
public enum EnumTextFormDataType {

    /**
     * Text
     */
    TEXT("Text"),
    /**
     * File
     */
    FILE("File");

    private final String type;

    EnumTextFormDataType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
