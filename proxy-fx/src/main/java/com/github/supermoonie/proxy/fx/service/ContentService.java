package com.github.supermoonie.proxy.fx.service;

import com.github.supermoonie.proxy.fx.entity.Content;
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
     * @param uri {@link com.github.supermoonie.proxy.fx.entity.Request#getUri()}
     * @return {@link Content}
     */
    Content saveContent(ByteBuf buf, String uri);
}
