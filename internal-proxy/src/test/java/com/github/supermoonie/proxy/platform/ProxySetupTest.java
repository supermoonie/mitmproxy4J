package com.github.supermoonie.proxy.platform;

import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/9/23
 */
public class ProxySetupTest {

    @Test
    public void getInterfacesInfo() throws SocketException {
        Map<String, List<String>> interfacesInfo = ProxySetup.getInterfacesInfo();
        System.out.println(interfacesInfo);
    }

    @Test
    public void enable() throws IOException {
        ProxySetup.enableHttpProxy("127.0.0.1", 7890, null, null);
        ProxySetup.enableSocksProxy("127.0.0.1", 7891, null, null);
    }

    @Test
    public void disable() throws IOException {
        ProxySetup.disableHttpProxy();
        ProxySetup.disableSocksProxy();
    }
}