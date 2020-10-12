package com.github.supermoonie.proxy.fx.service;

import io.netty.handler.codec.http.HttpHeaders;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
public interface HeaderService {

    /**
     * 保存HttpHeaders
     *
     * @param headers    {@link HttpHeaders}
     * @param requestId  {@link com.github.supermoonie.proxy.fx.entity.Request#getId()}
     * @param responseId {@link com.github.supermoonie.proxy.fx.entity.Response#getId()}
     * @return 数量
     */
    int saveHeaders(HttpHeaders headers, String requestId, String responseId);
}
