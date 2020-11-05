package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;

import java.util.List;
import java.util.Set;

/**
 * @author supermoonie
 * @since 2020/9/7
 */
public class BlackUriListProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            LoggingIntercept loggingIntercept = new LoggingIntercept();
            requestIntercepts.put("logging", loggingIntercept);
            responseIntercepts.put("logging", loggingIntercept);
            ConfigurableIntercept config = new ConfigurableIntercept();
            Set<String> blackList = config.getBlockUriList();
            blackList.add("https://httpbin.org/get");
            requestIntercepts.put("block-intercept", config);
        });
        proxy.start();
    }
}
