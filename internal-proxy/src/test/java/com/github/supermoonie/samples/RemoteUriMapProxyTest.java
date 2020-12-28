package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import com.github.supermoonie.proxy.intercept.RemoteMapIntercept;

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
            RemoteMapIntercept remoteMapIntercept = new RemoteMapIntercept();
            Map<String, String> remoteUriMap = remoteMapIntercept.getRemoteUriMap();
            remoteUriMap.put("https://www.baidu.com/", "https://httpbin.org/get");
            requestIntercepts.put("map-intercept", remoteMapIntercept);
        });
        proxy.start();
    }
}
