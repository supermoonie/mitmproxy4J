package com.github.supermoonie.proxy.intercept.req;

import com.github.supermoonie.proxy.intercept.InterceptContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

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
     * @return true continue, false break
     */
    FullHttpResponse onRequest(InterceptContext ctx, FullHttpRequest request);

    /**
     * on exception
     *
     * @param ctx     {@link InterceptContext}
     * @param request {@link FullHttpRequest}
     * @param ex      {@link Exception}
     * @return true continue, false stop
     * @throws Exception t
     */
    FullHttpResponse onException(InterceptContext ctx, FullHttpRequest request, Exception ex) throws Exception;
}
