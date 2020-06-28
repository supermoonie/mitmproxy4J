package com.github.supermoonie.service.impl;

import cn.hutool.core.lang.UUID;
import com.github.supermoonie.mapper.ContentMapper;
import com.github.supermoonie.model.Content;
import com.github.supermoonie.service.ContentService;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
@Service
@Slf4j
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
        content.setId(UUID.fastUUID().toString());
        buf.markReaderIndex();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.resetReaderIndex();
        content.setContent(bytes);
        contentMapper.insert(content);
        log.info("saved content: {}, uri: {}", content.getId(), uri);
        return content;
    }
}
