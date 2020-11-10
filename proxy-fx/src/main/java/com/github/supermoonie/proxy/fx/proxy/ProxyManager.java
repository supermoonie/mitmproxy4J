package com.github.supermoonie.proxy.fx.proxy;

import com.github.supermoonie.proxy.InterceptInitializer;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.platform.mac.NetworkSetup;
import io.netty.util.internal.PlatformDependent;

import java.io.IOException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
public final class ProxyManager {

    private static InternalProxy internalProxy;

    private ProxyManager() {

    }

    public static void start(int port, InterceptInitializer interceptInitializer) {
        if (null != internalProxy) {
            return;
        }
        internalProxy = new InternalProxy(interceptInitializer);
        internalProxy.setPort(port);
        internalProxy.start();
        internalProxy.getTrafficShapingHandler().setCheckInterval(1_000);
        internalProxy.getTrafficShapingHandler().setWriteLimit(GlobalSetting.getInstance().getThrottlingWriteLimit());
        internalProxy.getTrafficShapingHandler().setReadLimit(GlobalSetting.getInstance().getThrottlingReadLimit());
    }

    public static void restart(int port, InterceptInitializer interceptInitializer) {
        internalProxy.close();
        internalProxy = new InternalProxy(interceptInitializer);
        internalProxy.setPort(port);
        internalProxy.start();
        internalProxy.getTrafficShapingHandler().setCheckInterval(1_000);
        internalProxy.getTrafficShapingHandler().setWriteLimit(GlobalSetting.getInstance().getThrottlingWriteLimit());
        internalProxy.getTrafficShapingHandler().setReadLimit(GlobalSetting.getInstance().getThrottlingReadLimit());
    }

    public static void enableSystemProxy() throws IOException {
        if (null == internalProxy) {
            return;
        }
        String username = internalProxy.getUsername();
        String password = internalProxy.getPassword();
        int port = internalProxy.getPort();
        String host = "127.0.0.1";
        if (PlatformDependent.isOsx()) {
            List<String> allNetworkServices = NetworkSetup.listAllNetworkServices();
            boolean authenticated = null != username && !"".equals(username) && null != password && !"".equals(password);
            for (String networkService : allNetworkServices) {
                if (networkService.toLowerCase().contains("wi-fi") || networkService.toLowerCase().contains("lan")) {
                    NetworkSetup.setWebProxy(networkService, host, port, authenticated, username, password);
                    NetworkSetup.setSecureWebProxy(networkService, host, port, authenticated, username, password);
//                    NetworkSetup.setSocksFirewallProxy(networkService, host, port, authenticated, username, password);
                }
            }
        } else if (PlatformDependent.isWindows()) {
            // TODO
        }
    }

    public static void disableSystemProxy() throws IOException {
        if (PlatformDependent.isWindows()) {
            // TODO
        } else if (PlatformDependent.isOsx()) {
            List<String> allNetworkServices = NetworkSetup.listAllNetworkServices();
            for (String networkService : allNetworkServices) {
                if (networkService.toLowerCase().contains("wi-fi") || networkService.toLowerCase().contains("lan")) {
                    NetworkSetup.setWebProxyState(networkService, false);
                    NetworkSetup.setSecureWebProxyState(networkService, false);
//                    NetworkSetup.setSocksFirewallProxyState(networkService, false);
                }
            }
        }
    }

    public static void stop() {
        if (null == internalProxy) {
            return;
        }
        internalProxy.close();
    }

    public static InternalProxy getInternalProxy() {
        return internalProxy;
    }
}
