package com.github.supermoonie.proxy.platform.mac;

import com.github.supermoonie.proxy.platform.mac.info.HardwareOverview;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/9/23
 */
public class SystemProfilerTest {

    @Test
    public void SPHardwareDataType() throws IOException {
        HardwareOverview hardwareOverview = SystemProfiler.SPHardwareDataType();
        System.out.println(hardwareOverview);
    }
}