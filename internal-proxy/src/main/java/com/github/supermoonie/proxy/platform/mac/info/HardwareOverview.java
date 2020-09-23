package com.github.supermoonie.proxy.platform.mac.info;

/**
 * @author supermoonie
 * @since 2020/9/23
 */
public class HardwareOverview {

    private String modelName;

    private String modelIdentifier;

    private String processorName;

    private String processorSpeed;

    private int numberOfProcessors;

    private int totalNumberOfCores;

    private String l2CachePerCore;

    private String l3Cache;

    private String hyperThreadingTechnology;

    private String memory;

    private String bootRomVersion;

    private String serialNumber;

    private String hardwareUUID;

    private String activationLockStatus;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public String getProcessorSpeed() {
        return processorSpeed;
    }

    public void setProcessorSpeed(String processorSpeed) {
        this.processorSpeed = processorSpeed;
    }

    public int getNumberOfProcessors() {
        return numberOfProcessors;
    }

    public void setNumberOfProcessors(int numberOfProcessors) {
        this.numberOfProcessors = numberOfProcessors;
    }

    public int getTotalNumberOfCores() {
        return totalNumberOfCores;
    }

    public void setTotalNumberOfCores(int totalNumberOfCores) {
        this.totalNumberOfCores = totalNumberOfCores;
    }

    public String getL2CachePerCore() {
        return l2CachePerCore;
    }

    public void setL2CachePerCore(String l2CachePerCore) {
        this.l2CachePerCore = l2CachePerCore;
    }

    public String getL3Cache() {
        return l3Cache;
    }

    public void setL3Cache(String l3Cache) {
        this.l3Cache = l3Cache;
    }

    public String getHyperThreadingTechnology() {
        return hyperThreadingTechnology;
    }

    public void setHyperThreadingTechnology(String hyperThreadingTechnology) {
        this.hyperThreadingTechnology = hyperThreadingTechnology;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getBootRomVersion() {
        return bootRomVersion;
    }

    public void setBootRomVersion(String bootRomVersion) {
        this.bootRomVersion = bootRomVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getHardwareUUID() {
        return hardwareUUID;
    }

    public void setHardwareUUID(String hardwareUUID) {
        this.hardwareUUID = hardwareUUID;
    }

    public String getActivationLockStatus() {
        return activationLockStatus;
    }

    public void setActivationLockStatus(String activationLockStatus) {
        this.activationLockStatus = activationLockStatus;
    }

    @Override
    public String toString() {
        return "HardwareOverview{" +
                "modelName='" + modelName + '\'' +
                ", modelIdentifier='" + modelIdentifier + '\'' +
                ", processorName='" + processorName + '\'' +
                ", processorSpeed='" + processorSpeed + '\'' +
                ", numberOfProcessors=" + numberOfProcessors +
                ", totalNumberOfCores=" + totalNumberOfCores +
                ", l2CachePerCore='" + l2CachePerCore + '\'' +
                ", l3Cache='" + l3Cache + '\'' +
                ", hyperThreadingTechnology='" + hyperThreadingTechnology + '\'' +
                ", memory='" + memory + '\'' +
                ", bootRomVersion='" + bootRomVersion + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", hardwareUUID='" + hardwareUUID + '\'' +
                ", activationLockStatus='" + activationLockStatus + '\'' +
                '}';
    }
}
