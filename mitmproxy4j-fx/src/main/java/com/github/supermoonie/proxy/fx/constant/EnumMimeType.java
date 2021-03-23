package com.github.supermoonie.proxy.fx.constant;

/**
 * @author supermoonie
 * @since 2021/3/23
 */
public enum EnumMimeType {

    /**
     * mime type
     */
    FORM_DATA("form-data"),
    FORM_URL_ENCODED("x-www-form-urlencoded"),
    BINARY("binary"),
    RAW("raw");
    private final String value;

    EnumMimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EnumMimeType{" +
                "value='" + value + '\'' +
                '}';
    }
}
