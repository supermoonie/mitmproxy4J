package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @since 2020/9/6
 */
public interface ExceptionHandler {

    /**
     * caught exception
     *
     * @param ctx   {@link InterceptContext}
     * @param cause {@link Throwable}
     * @return {@link FullHttpResponse}
     */
    FullHttpResponse onException(InterceptContext ctx, Throwable cause);
}
