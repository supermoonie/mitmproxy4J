package com.github.supermoonie.proxy.swing.service;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.Request;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.ibatis.session.SqlSession;

import java.util.UUID;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public final class RequestService {

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
    public static Request saveRequest(InterceptContext ctx, HttpRequest httpRequest) {
        ConnectionInfo connectionInfo = ctx.getConnectionInfo();
        String requestId = UUID.randomUUID().toString();
        String host = connectionInfo.getRemoteHost();
        int port = connectionInfo.getRemotePort();
        HttpMethod method = httpRequest.method();
        HttpVersion httpVersion = httpRequest.protocolVersion();
        String contentType = httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE);
        Request req = new Request();
//        req.setId(requestId);
        req.setUri(connectionInfo.getUrl());
        req.setMethod(method.name());
        req.setHttpVersion(httpVersion.text());
        req.setContentType(contentType);
        req.setHost(host);
        req.setPort(port);
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {

        }
        return null;
    }
}
