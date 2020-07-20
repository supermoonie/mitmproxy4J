package com.github.supermoonie.constant;

/**
 * @author supermoonie
 * @since 2020/7/9
 */
public enum EnumFlowNodeType {
    /**
     * BASE_URL
     */
    BASE_URL("BASE_URL"),
    /**
     * PATH
     */
    PATH("PATH"),
    /**
     * LAST PATH
     */
    TARGET("TARGET");

    private final String des;

    EnumFlowNodeType(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return des;
    }
}
