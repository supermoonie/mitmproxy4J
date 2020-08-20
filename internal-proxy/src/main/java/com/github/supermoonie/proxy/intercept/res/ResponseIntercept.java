package com.github.supermoonie.proxy.intercept.res;

import com.github.supermoonie.proxy.intercept.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public interface ResponseIntercept {

    /**
     * on response
     *
     * @param ctx      {@link InterceptContext}
     * @param response response msg
     * @return true continue, false stop
     */
    boolean onResponse(InterceptContext ctx, FullHttpResponse response);

    /**
     * on exception
     *
     * @param ctx      {@link InterceptContext}
     * @param response {@link FullHttpResponse}
     * @param ex       {@link Exception}
     * @return true continue, false stop
     * @throws Exception t
     */
    boolean onException(InterceptContext ctx, FullHttpResponse response, Exception ex) throws Exception;
}
