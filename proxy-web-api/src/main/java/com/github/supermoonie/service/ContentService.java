package com.github.supermoonie.service;

import com.github.supermoonie.model.Content;
import com.github.supermoonie.model.Request;
import io.netty.buffer.ByteBuf;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
public interface ContentService {

    /**
     * 保存Content
     *
     * @param buf {@link ByteBuf}
     * @param uri {@link Request#getUri()}
     * @return {@link Content}
     */
    Content saveContent(ByteBuf buf, String uri);
}
