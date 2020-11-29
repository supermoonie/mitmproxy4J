package com.github.supermoonie.proxy.swing.gui.flow;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
public enum FlowType {

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

    FlowType(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return des;
    }
}
