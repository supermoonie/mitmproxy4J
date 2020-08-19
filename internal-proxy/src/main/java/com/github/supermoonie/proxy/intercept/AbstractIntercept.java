package com.github.supermoonie.proxy.intercept;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @since 2020/8/12
 */
public abstract class AbstractIntercept implements Intercept {

    AbstractIntercept pre;

    AbstractIntercept next;

    @Override
    public void onActive(InterceptContext ctx) {
        if (null != next) {
            next.onActive(ctx);
        }
    }

    @Override
    public boolean onRequest(InterceptContext ctx, FullHttpRequest request) {
        if (null != next) {
            return next.onRequest(ctx, request);
        }
        return true;
    }

    @Override
    public void onResponse(InterceptContext ctx, FullHttpResponse response) {
        Intercept intercept = next;
        if (null != intercept) {
            intercept.onResponse(ctx, response);
        }
    }

    @Override
    public void onException(InterceptContext ctx, Exception ex) throws Exception {
        Intercept intercept = next;
        if (null != intercept) {
            intercept.onException(ctx, ex);
        }
    }
}
