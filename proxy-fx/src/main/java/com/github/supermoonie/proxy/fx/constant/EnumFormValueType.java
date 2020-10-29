package com.github.supermoonie.proxy.fx.constant;

/**
 * @author supermoonie
 * @since 2020/10/19
 */
public enum EnumFormValueType {
    /**
     * form-data type
     */
    TEXT("Text"), FILE("File");

    private final String value;

    EnumFormValueType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
