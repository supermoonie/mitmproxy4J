package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.service.*;
import io.netty.buffer.ByteBufUtil;
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
import sun.security.x509.X509CertImpl;

import javax.annotation.Resource;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.List;
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

    @Resource
    private ConnectionOverviewService connectionOverviewService;

    @Resource
    private CertificateInfoService certificateInfoService;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Request saveRequest(InterceptContext ctx, HttpRequest httpRequest) {
        ConnectionInfo connectionInfo = ctx.getConnectionInfo();
        Assert.notNull(connectionInfo, "ConnectionInfo is null!");
        String requestId = UUID.randomUUID().toString();
        String host = connectionInfo.getRemoteHost();
        int port = connectionInfo.getRemotePort();
        HttpMethod method = httpRequest.method();
        HttpVersion httpVersion = httpRequest.protocolVersion();
        String contentType = httpRequest.headers().get(HttpHeaders.CONTENT_TYPE);
        Request req = new Request();
        req.setId(requestId);
        req.setUri(connectionInfo.getUrl());
        req.setMethod(method.name());
        req.setHttpVersion(httpVersion.text());
        req.setContentType(contentType);
        req.setHost(host);
        req.setPort(port);
        req.setStartTime(connectionInfo.getRequestStartTime());
        req.setEndTime(connectionInfo.getRequestEndTime());
        req.setTimeCreated(new Date());
        if (httpRequest instanceof FullHttpRequest) {
            Content content = contentService.saveContent(((FullHttpRequest) httpRequest).content(), req.getUri());
            req.setContentId(content.getId());
        }
        requestMapper.insert(req);
        connectionOverviewService.saveClientInfo(connectionInfo, requestId);
        List<Certificate> localCertificates = connectionInfo.getLocalCertificates();
        certificateInfoService.saveList(localCertificates, requestId, null);
        headerService.saveHeaders(httpRequest.headers(), requestId, null);
        return req;
    }

    private void saveCertificate(Certificate certificate) {
        X509CertImpl cert = (X509CertImpl) certificate;
        log.info(cert.getName());
    }
}
