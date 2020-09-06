package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author supermoonie
 * @since 2020/9/6
 */
public class LoggingIntercept implements RequestIntercept, ResponseIntercept {

    private static final Logger logger = LoggerFactory.getLogger(LoggingIntercept.class);

    @Override
    public FullHttpResponse onException(InterceptContext ctx, Throwable cause) {
        logger.error(cause.getMessage(), cause);
        return null;
    }

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        logger.info(">>>> {}\n", request.toString());
        return null;
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, FullHttpResponse response) {
        logger.info("<<<< {}\n", response.toString());
        return null;
    }
}
