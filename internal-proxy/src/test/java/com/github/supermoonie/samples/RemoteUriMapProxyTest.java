package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;

import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/9/7
 */
public class RemoteUriMapProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            LoggingIntercept loggingIntercept = new LoggingIntercept();
            requestIntercepts.put("logging", loggingIntercept);
            responseIntercepts.put("logging", loggingIntercept);
            ConfigurableIntercept config = new ConfigurableIntercept();
            Map<String, String> remoteUriMap = config.getRemoteUriMap();
            remoteUriMap.put("https://www.baidu.com/", "https://httpbin.org/get");
            requestIntercepts.put("map-intercept", config);
        });
        proxy.start();
    }
}
