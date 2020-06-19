package com.github.supermoonie.service.impl;

import cn.hutool.core.lang.UUID;
import com.github.supermoonie.mapper.ResponseMapper;
import com.github.supermoonie.model.Content;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import com.github.supermoonie.service.ContentService;
import com.github.supermoonie.service.HeaderService;
import com.github.supermoonie.service.ResponseService;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
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
public class ResponseServiceImpl implements ResponseService {

    @Resource
    private HeaderService headerService;

    @Resource
    private ResponseMapper responseMapper;

    @Resource
    private ContentService contentService;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Response saveResponse(FullHttpResponse httpResponse, Request request) {
        Content content = contentService.saveContent(httpResponse.content(), request.getUri());
        Response res = new Response();
        res.setId(UUID.fastUUID().toString());
        res.setRequestId(request.getId());
        res.setContentType(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE));
        res.setHttpVersion(httpResponse.protocolVersion().text());
        res.setStatus(httpResponse.status().code());
        res.setContentId(content.getId());
        responseMapper.insert(res);
        log.info("saved response: {}, uri: {}", res.getId(), request.getUri());
        headerService.saveHeaders(httpResponse.headers(), null, res.getId());
        return res;
    }
}
