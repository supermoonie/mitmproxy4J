package com.github.supermoonie.proxy.fx.service;

import com.github.supermoonie.proxy.fx.entity.Request;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
public interface RequestService {

    /**
     * 保存Request
     *
     * @param httpRequest {@link FullHttpRequest}
     * @return {@link Request}
     */
    Request saveRequest(HttpRequest httpRequest);
}
