package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public interface ResponseIntercept extends ExceptionHandler {

    /**
     * on response
     *
     * @param ctx      {@link InterceptContext}
     * @param response response msg
     * @return {@link FullHttpResponse}
     */
    FullHttpResponse onResponse(InterceptContext ctx, FullHttpResponse response);

    @Override
    default FullHttpResponse onException(InterceptContext ctx, Throwable cause) {
        return null;
    }
}
