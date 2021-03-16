package com.github.supermoonie.proxy.fx.service;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
public interface ResponseService {

    /**
     * 保存Response
     *
     * @param ctx {@link InterceptContext}
     * @param httpResponse {@link FullHttpResponse}
     * @param request      {@link Request}
     * @return {@link Response}
     */
    Response saveResponse(InterceptContext ctx, FullHttpResponse httpResponse, Request request);
}
