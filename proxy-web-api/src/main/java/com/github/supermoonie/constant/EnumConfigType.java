package com.github.supermoonie.constant;

/**
 * @author supermoonie
 * @since 2020/9/26
 */
public enum EnumConfigType {

    REMOTE_URI_MAP(1)
    ;

    private final int type;

    EnumConfigType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}
