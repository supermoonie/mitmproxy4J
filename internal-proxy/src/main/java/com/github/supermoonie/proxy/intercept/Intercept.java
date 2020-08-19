package com.github.supermoonie.proxy.intercept;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @since 2020/8/11
 */
public interface Intercept {

    /**
     * on active
     *
     * @param ctx {@link InterceptContext}
     */
    void onActive(InterceptContext ctx);

    /**
     * on request
     *
     * @param ctx     {@link InterceptContext}
     * @param request request msg
     * @return true continue, false break
     */
    boolean onRequest(InterceptContext ctx, FullHttpRequest request);

    /**
     * on response
     *
     * @param ctx      {@link InterceptContext}
     * @param response response msg
     */
    void onResponse(InterceptContext ctx, FullHttpResponse response);

    /**
     * on exception
     *
     * @param ctx {@link InterceptContext}
     * @param ex  {@link Exception}
     * @throws Exception t
     */
    void onException(InterceptContext ctx, Exception ex) throws Exception;
}
