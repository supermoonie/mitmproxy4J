package com.github.supermoonie.service;

import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
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
     * @param requestId  {@link Request#getId()}
     * @param responseId {@link Response#getId()}
     * @return 数量
     */
    int saveHeaders(HttpHeaders headers, String requestId, String responseId);
}
