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

    default FullHttpResponse onException(InterceptContext ctx, HttpRequest request, Throwable cause) {
        return null;
    }
}
