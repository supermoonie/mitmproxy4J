package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.service.ContentService;
import com.github.supermoonie.proxy.fx.service.HeaderService;
import com.github.supermoonie.proxy.fx.service.RequestService;
import com.github.supermoonie.proxy.util.RequestUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class RequestServiceImpl implements RequestService {

    private final Logger log = LoggerFactory.getLogger(RequestServiceImpl.class);

    @Resource
    private HeaderService headerService;

    @Resource
    private RequestMapper requestMapper;

    @Resource
    private ContentService contentService;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Request saveRequest(HttpRequest httpRequest) {
        ConnectionInfo connectionInfo = RequestUtils.parseRemoteInfo(httpRequest, null);
        Assert.notNull(connectionInfo, "ConnectionInfo is null!");
        String host = connectionInfo.getRemoteHost();
        int port = connectionInfo.getRemotePort();
        HttpMethod method = httpRequest.method();
        HttpVersion httpVersion = httpRequest.protocolVersion();
        String contentType = httpRequest.headers().get(HttpHeaders.CONTENT_TYPE);
        Request req = new Request();
        req.setId(UUID.randomUUID().toString());
        req.setUri(httpRequest.uri());
        req.setMethod(method.name());
        req.setHttpVersion(httpVersion.text());
        req.setContentType(contentType);
        req.setHost(host);
        req.setPort(port);
        if (httpRequest instanceof FullHttpRequest) {
            Content content = contentService.saveContent(((FullHttpRequest) httpRequest).content(), req.getUri());
            req.setContentId(content.getId());
        }
        requestMapper.insert(req);
        headerService.saveHeaders(httpRequest.headers(), req.getId(), null);
        return req;
    }
}
