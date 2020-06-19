package com.github.supermoonie.service;

import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
public interface ResponseService {

    /**
     * 保存Response
     *
     * @param httpResponse {@link FullHttpResponse}
     * @param request      {@link Request}
     * @return {@link Response}
     */
    Response saveResponse(FullHttpResponse httpResponse, Request request);
}
