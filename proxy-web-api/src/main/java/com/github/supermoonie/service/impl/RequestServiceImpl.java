package com.github.supermoonie.service.impl;

import cn.hutool.core.lang.UUID;
import com.github.supermoonie.mapper.RequestMapper;
import com.github.supermoonie.model.Content;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.service.ContentService;
import com.github.supermoonie.service.HeaderService;
import com.github.supermoonie.service.RequestService;
import com.github.supermoonie.util.ProtoUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
@Service
@Slf4j
@Transactional(rollbackFor = RuntimeException.class)
public class RequestServiceImpl implements RequestService {

    @Resource
    private HeaderService headerService;

    @Resource
    private RequestMapper requestMapper;

    @Resource
    private ContentService contentService;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Request saveRequest(FullHttpRequest httpRequest) {
        ProtoUtil.RequestProto proto = ProtoUtil.getRequestProto(httpRequest);
        Assert.notNull(proto, "RequestProto is null!");
        String host = proto.getHost();
        int port = proto.getPort();
        HttpMethod method = httpRequest.method();
        HttpVersion httpVersion = httpRequest.protocolVersion();
        Request req = new Request();
        req.setId(UUID.fastUUID().toString());
        req.setUri(httpRequest.uri());
        req.setMethod(method.name());
        req.setHttpVersion(httpVersion.text());
        req.setHost(host);
        req.setPort(port);
        Content content = contentService.saveContent(httpRequest.content(), req.getUri());
        req.setContentId(content.getId());
        requestMapper.insert(req);
        log.info("saved request: {}, uri: {}", req.getId(), req.getUri());
        headerService.saveHeaders(httpRequest.headers(), req.getId(), null);
        return req;
    }
}
