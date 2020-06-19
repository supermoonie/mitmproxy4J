package com.github.supermoonie.service;

import com.github.supermoonie.model.Request;
import io.netty.handler.codec.http.FullHttpRequest;

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
    Request saveRequest(FullHttpRequest httpRequest);
}
