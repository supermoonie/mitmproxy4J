package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author supermoonie
 * @since 2021/1/24
 */
public class ExternalProxyIntercept implements RequestIntercept {

    public static final ExternalProxyIntercept INSTANCE = new ExternalProxyIntercept();

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {

        return null;
    }
}
