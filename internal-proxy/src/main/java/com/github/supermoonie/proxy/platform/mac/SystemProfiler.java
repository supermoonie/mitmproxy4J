package com.github.supermoonie.proxy.platform.mac;

import com.github.supermoonie.proxy.platform.mac.info.HardwareOverview;
import com.github.supermoonie.proxy.util.ExecUtils;

import java.io.IOException;

/**
 * @author supermoonie
 * @since 2020/9/23
 */
public final class SystemProfiler {

    private static final String SYSTEM_PROFILER = "system_profiler";

    private SystemProfiler() {
    }

    public static HardwareOverview SPHardwareDataType() throws IOException {
        String result = ExecUtils.exec(SYSTEM_PROFILER, "SPHardwareDataType");
        String[] lines = result.split("\r?\n");
        HardwareOverview hardwareOverview = new HardwareOverview();
        for (String line : lines) {
            if (line.trim().startsWith("Model Name")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setModelName(arr[1]);
                }
            } else if (line.trim().startsWith("Model Identifier")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setModelIdentifier(arr[1]);
                }
            } else if (line.trim().startsWith("Processor Name")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setProcessorName(arr[1]);
                }
            } else if (line.trim().startsWith("Processor Speed")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setProcessorSpeed(arr[1]);
                }
            } else if (line.trim().startsWith("Number of Processors")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setNumberOfProcessors(Integer.parseInt(arr[1]));
                }
            } else if (line.trim().startsWith("Total Number of Cores")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setTotalNumberOfCores(Integer.parseInt(arr[1]));
                }
            } else if (line.trim().startsWith("L2 Cache (per Core)")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setL2CachePerCore(arr[1]);
                }
            } else if (line.trim().startsWith("L3 Cache")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setL3Cache(arr[1]);
                }
            } else if (line.trim().startsWith("Hyper-Threading Technology")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setHyperThreadingTechnology(arr[1]);
                }
            } else if (line.trim().startsWith("Memory")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setMemory(arr[1]);
                }
            } else if (line.trim().startsWith("Boot ROM Version")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setBootRomVersion(arr[1]);
                }
            } else if (line.trim().startsWith("Serial Number (system)")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setSerialNumber(arr[1]);
                }
            } else if (line.trim().startsWith("Hardware UUID")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setHardwareUUID(arr[1]);
                }
            } else if (line.trim().startsWith("Activation Lock Status")) {
                String[] arr = line.split(": ");
                if (arr.length > 1) {
                    hardwareOverview.setActivationLockStatus(arr[1]);
                }
            }
        }
        return hardwareOverview;
    }
}
