package com.github.supermoonie.proxy.fx.proxy;

import com.github.supermoonie.proxy.InterceptInitializer;
import com.github.supermoonie.proxy.InternalProxy;

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
