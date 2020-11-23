package com.github.supermoonie.proxy.swing.service;

import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public final class ResponseService {

    private ResponseService() {
        throw new UnsupportedOperationException();
    }

    public static Response saveResponse(InterceptContext ctx, Request request, FullHttpResponse response) {
        return null;
    }
}
