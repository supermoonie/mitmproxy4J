package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.mapper.ContentMapper;
import com.github.supermoonie.proxy.fx.service.ContentService;
import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class ContentServiceImpl implements ContentService {

    @Resource
    private ContentMapper contentMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Content saveContent(ByteBuf buf, String uri) {
        Content content = new Content();
        if (0 == buf.readableBytes()) {
            return content;
        }
        content.setId(UUID.randomUUID().toString());
        buf.markReaderIndex();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.resetReaderIndex();
        content.setContent(bytes);
        contentMapper.insert(content);
        return content;
    }
}
