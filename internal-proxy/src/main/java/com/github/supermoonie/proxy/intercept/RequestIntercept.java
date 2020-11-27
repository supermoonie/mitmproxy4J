package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public interface RequestIntercept {

    /**
     * on request
     *
     * @param ctx     {@link InterceptContext}
     * @param request request msg
     * @return null continue, else break
     */
    FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request);

    /**
     * on request write
     *
     * @param ctx     {@link InterceptContext}
     * @param request request msg
     */
    default void onWrite(InterceptContext ctx, HttpRequest request) {

    }

    /**
     * exception occur when send request
     *
     * @param ctx     {@link InterceptContext}
     * @param request request msg
     * @param cause   exception
     * @return response
     */
    default FullHttpResponse onException(InterceptContext ctx, HttpRequest request, Throwable cause) {
        return null;
    }
}
