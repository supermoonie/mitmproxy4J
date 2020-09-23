package com.github.supermoonie.proxy.platform.mac;

/**
 * @author supermoonie
 * @since 2020/9/21
 */
public class HardwarePortInfo {

    private String hardwarePort;

    private String device;

    private String ethernetAddress;

    public String getHardwarePort() {
        return hardwarePort;
    }

    public void setHardwarePort(String hardwarePort) {
        this.hardwarePort = hardwarePort;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEthernetAddress() {
        return ethernetAddress;
    }

    public void setEthernetAddress(String ethernetAddress) {
        this.ethernetAddress = ethernetAddress;
    }

    @Override
    public String toString() {
        return "HardwarePortInfo{" +
                "hardwarePort='" + hardwarePort + '\'' +
                ", device='" + device + '\'' +
                ", ethernetAddress='" + ethernetAddress + '\'' +
                '}';
    }
}
