package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public interface ResponseIntercept {

    /**
     * on response
     *
     * @param ctx      {@link InterceptContext}
     * @param request  {@link HttpRequest}
     * @param response response msg
     * @return {@link FullHttpResponse}
     */
    FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response);

    /**
     * on read
     *
     * @param ctx      {@link InterceptContext}
     * @param request  {@link HttpRequest}
     */
    default void onRead(InterceptContext ctx, HttpRequest request) {

    }

    default FullHttpResponse onException(InterceptContext ctx, HttpRequest request, FullHttpResponse response, Throwable cause) {
        return null;
    }
}
