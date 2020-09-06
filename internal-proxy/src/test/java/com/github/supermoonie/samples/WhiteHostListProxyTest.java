package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/9/7
 */
public class WhiteHostListProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            LoggingIntercept loggingIntercept = new LoggingIntercept();
            requestIntercepts.put("logging", loggingIntercept);
            responseIntercepts.put("logging", loggingIntercept);
            ConfigurableIntercept config = new ConfigurableIntercept();
            List<String> whiteList = config.getWhiteHostList();
            whiteList.add("httpbin.org");
            requestIntercepts.put("block-intercept", config);
        });
        proxy.start();
    }
}
