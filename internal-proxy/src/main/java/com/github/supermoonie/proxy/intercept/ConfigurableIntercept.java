package com.github.supermoonie.proxy.intercept;


import com.github.supermoonie.proxy.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public class ConfigurableIntercept implements RequestIntercept, ResponseIntercept {

    private List<String> blackList;

    private List<String> whiteList;

    private Map<String, String> remoteMap;

    private Map<String, String> localMap;

    private String userName;

    private String password;

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        return null;
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, FullHttpResponse response) {
        return null;
    }

    @Override
    public FullHttpResponse onException(InterceptContext ctx, Throwable cause) {
        return null;
    }
}
