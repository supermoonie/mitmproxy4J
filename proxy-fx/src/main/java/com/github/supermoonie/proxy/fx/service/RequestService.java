package com.github.supermoonie.proxy.fx.service;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public final class RequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);

    private RequestService() {
        throw new UnsupportedOperationException();
    }

    /**
     * save request
     *
     * @param ctx         {@link InterceptContext}
     * @param httpRequest {@link HttpRequest}
     * @return {@link Request}
     */
    public static Request saveRequest(InterceptContext ctx, HttpRequest httpRequest) throws SQLException {
        ConnectionInfo connectionInfo = ctx.getConnectionInfo();
        String host = connectionInfo.getRemoteHost();
        int port = connectionInfo.getRemotePort();
        HttpMethod method = httpRequest.method();
        HttpVersion httpVersion = httpRequest.protocolVersion();
        String contentType = httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);
        Request req = new Request();
        req.setMethod(method.name());
        req.setHost(host);
        req.setPort(port);
        req.setUri(connectionInfo.getUrl());
        req.setHttpVersion(httpVersion.text());
        req.setContentType(contentType);
        req.setTimeCreated(new Date());
        TransactionManager transactionManager = new TransactionManager(DaoCollections.getConnectionSource());
        return transactionManager.callInTransaction(() -> {
            if (httpRequest instanceof FullHttpRequest) {
                int contentId = ContentService.saveContent(((FullHttpRequest) httpRequest).content());
                req.setContentId(contentId);
            }
            Dao<Request, Integer> dao = DaoCollections.getDao(Request.class);
            dao.create(req);
            ConnectionOverviewService.saveClientInfo(connectionInfo, req.getId());
            CertificateInfoService.saveList(connectionInfo.getLocalCertificates(), req.getId(), null);
            HeaderService.saveHeaders(httpRequest.headers(), req.getId(), null);
            return req;
        });
    }
}
