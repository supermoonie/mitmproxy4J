package com.github.supermoonie.constant;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
public enum EnumYesNo {

    /**
     * yes or no
     */
    YES(1), NO(0);

    private final int value;

    EnumYesNo(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EnumYesNo{" +
                "value=" + value +
                '}';
    }
}
