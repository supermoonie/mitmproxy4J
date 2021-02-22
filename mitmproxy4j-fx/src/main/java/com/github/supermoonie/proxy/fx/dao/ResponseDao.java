package com.github.supermoonie.proxy.fx.dao;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.util.BrUtil;
import com.j256.ormlite.dao.Dao;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.cert.Certificate;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public final class ResponseDao {

    private static final Logger log = LoggerFactory.getLogger(ResponseDao.class);

    private ResponseDao() {
        throw new UnsupportedOperationException();
    }

    public static Response saveResponse(InterceptContext ctx, Request request, FullHttpResponse response) throws SQLException {
        ConnectionInfo connectionInfo = ctx.getConnectionInfo();
        ConnectionOverviewDao.updateServerInfo(connectionInfo, request.getId());
        Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
        request.setStartTime(0 == connectionInfo.getRequestStartTime() ? null : connectionInfo.getRequestStartTime());
        request.setEndTime(0 == connectionInfo.getRequestEndTime() ? null : connectionInfo.getRequestEndTime());
        requestDao.update(request);
        String contentEncoding = response.headers().get(HttpHeaderNames.CONTENT_ENCODING);
        ByteBuf buf;
        boolean releaseFlag = false;
        if ("br".equalsIgnoreCase(contentEncoding)) {
            ByteBuf byteBuf = response.content();
            byteBuf.markReaderIndex();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            byteBuf.resetReaderIndex();
            try {
                byte[] decompress = BrUtil.decompress(bytes, true);
                buf = Unpooled.wrappedBuffer(decompress);
                releaseFlag = true;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        } else {
            buf = response.content();
        }
        int contentId = ContentDao.saveContent(buf);
        if (releaseFlag) {
            buf.release();
        }
        Response res = new Response();
        res.setRequestId(request.getId());
        res.setContentType(response.headers().get(HttpHeaderNames.CONTENT_TYPE));
        res.setHttpVersion(response.protocolVersion().text());
        res.setStatus(response.status().code());
        res.setContentId(contentId);
        res.setStartTime(0 == connectionInfo.getResponseStartTime() ? null : connectionInfo.getResponseStartTime());
        res.setEndTime(0 == connectionInfo.getResponseEndTime() ? null : connectionInfo.getResponseEndTime());
        res.setTimeCreated(new Date());
        Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
        responseDao.create(res);
        HeaderDao.saveHeaders(response.headers(), request.getId(), res.getId());
        List<Certificate> serverCertificates = connectionInfo.getServerCertificates();
        CertificateInfoDao.saveList(serverCertificates, request.getId(), res.getId());
        return res;
    }
}
