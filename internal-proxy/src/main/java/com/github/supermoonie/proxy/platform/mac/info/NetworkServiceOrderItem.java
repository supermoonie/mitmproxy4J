package com.github.supermoonie.proxy.platform.mac.info;

/**
 * @author supermoonie
 * @since 2020/9/21
 */
public class NetworkServiceOrderItem {

    private String name;

    private Integer order;

    private String device;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "NetworkServiceOrderItem{" +
                "name='" + name + '\'' +
                ", order=" + order +
                ", device='" + device + '\'' +
                '}';
    }
}
