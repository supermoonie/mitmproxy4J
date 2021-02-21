package com.github.supermoonie.proxy.fx.constant;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
public enum EnumFlowType {

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

    EnumFlowType(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return des;
    }
}
