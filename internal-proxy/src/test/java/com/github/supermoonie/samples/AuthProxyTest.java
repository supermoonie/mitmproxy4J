package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import com.github.supermoonie.util.ResponseUtils;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author supermoonie
 * @since 2020/9/6
 */
public class AuthProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy((requestIntercepts, responseIntercepts) -> {
            LoggingIntercept loggingIntercept = new LoggingIntercept();
            requestIntercepts.put("logging", loggingIntercept);
            requestIntercepts.put("intercept-0", (ctx, request) -> ResponseUtils.htmlResponse("Hello Mitmproxy4J!", HttpResponseStatus.OK));
            responseIntercepts.put("logging", loggingIntercept);
        });
//        proxy.setUsername("foo");
//        proxy.setPassword("bar");
        proxy.start();
    }
}
