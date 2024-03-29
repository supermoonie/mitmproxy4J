package com.github.supermoonie.proxy.swing.proxy;

import com.github.supermoonie.proxy.InterceptInitializer;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.platform.mac.NetworkSetup;
import com.github.supermoonie.proxy.swing.MitmProxy4J;
import io.netty.util.internal.PlatformDependent;

import java.io.IOException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
public class ProxyManager {

    private static InternalProxy internalProxy;

    private ProxyManager() {

    }

    public static void start(int port,
                             boolean auth,
                             String username,
                             String password,
                             InterceptInitializer interceptInitializer) {
        if (null != internalProxy) {
            return;
        }
        newHttpProxy(port, auth, username, password, interceptInitializer);
    }

    private static void newHttpProxy(int port,
                                     boolean auth,
                                     String username,
                                     String password,
                                     InterceptInitializer interceptInitializer) {
        internalProxy = new InternalProxy(interceptInitializer);
        internalProxy.setHost(null);
        internalProxy.setPort(port);
        internalProxy.setAuth(auth);
        internalProxy.setUsername(username);
        internalProxy.setPassword(password);
        internalProxy.start();
        internalProxy.getTrafficShapingHandler().setCheckInterval(1_000);
    }

    public static void restart(int port,
                               boolean auth,
                               String username,
                               String password,
                               InterceptInitializer interceptInitializer) {
        InternalProxy oldProxy = internalProxy;
        newHttpProxy(port, auth, username, password, interceptInitializer);
        MitmProxy4J.EXECUTOR.execute(oldProxy::close);
    }

    public static void enableLimit(boolean enable) {
        internalProxy.setTrafficShaping(enable);
    }

    public static void setWriteLimit(long writeLimit) {
        internalProxy.getTrafficShapingHandler().setWriteLimit(writeLimit);
        internalProxy.getTrafficShapingHandler().setWriteChannelLimit(writeLimit);
    }

    public static void setReadLimit(long readLimit) {
        internalProxy.getTrafficShapingHandler().setReadLimit(readLimit);
        internalProxy.getTrafficShapingHandler().setReadChannelLimit(readLimit);
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
