package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
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
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        logger.info(">>>> {}\n", request.toString());
        return null;
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        logger.info("<<<< {}, {}\n", request.uri(), response.toString());
        return null;
    }

    @Override
    public FullHttpResponse onException(InterceptContext ctx, HttpRequest request, Throwable cause) {
        logger.error("uri: {}, error: {}", request.uri(), cause.getMessage(), cause);
        return null;
    }

    @Override
    public FullHttpResponse onException(InterceptContext ctx, HttpRequest request, FullHttpResponse response, Throwable cause) {
        logger.error("uri: {}, error: {}", request.uri(), cause.getMessage(), cause);
        return null;
    }
}
