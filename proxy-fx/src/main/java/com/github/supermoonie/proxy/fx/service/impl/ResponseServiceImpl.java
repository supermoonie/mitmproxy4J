package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.mapper.ResponseMapper;
import com.github.supermoonie.proxy.fx.service.ContentService;
import com.github.supermoonie.proxy.fx.service.HeaderService;
import com.github.supermoonie.proxy.fx.service.ResponseService;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ResponseServiceImpl implements ResponseService {

    private final Logger log = LoggerFactory.getLogger(ResponseServiceImpl.class);

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
        res.setId(UUID.randomUUID().toString());
        res.setRequestId(request.getId());
        res.setContentType(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE));
        res.setHttpVersion(httpResponse.protocolVersion().text());
        res.setStatus(httpResponse.status().code());
        res.setContentId(content.getId());
        responseMapper.insert(res);
        headerService.saveHeaders(httpResponse.headers(), null, res.getId());
        return res;
    }
}
