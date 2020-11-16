package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.mapper.ResponseMapper;
import com.github.supermoonie.proxy.fx.service.*;
import com.github.supermoonie.proxy.fx.util.BrotliUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.List;
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
    private RequestMapper requestMapper;

    @Resource
    private HeaderService headerService;

    @Resource
    private ResponseMapper responseMapper;

    @Resource
    private ContentService contentService;

    @Resource
    private ConnectionOverviewService connectionOverviewService;

    @Resource
    private CertificateInfoService certificateInfoService;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Response saveResponse(InterceptContext ctx, FullHttpResponse httpResponse, Request request) {
        ConnectionInfo connectionInfo = ctx.getConnectionInfo();
        request.setStartTime(connectionInfo.getRequestStartTime());
        request.setEndTime(connectionInfo.getRequestEndTime());
        requestMapper.updateById(request);
        connectionOverviewService.updateServerInfo(connectionInfo, request.getId());
        String contentEncoding = httpResponse.headers().get(HttpHeaderNames.CONTENT_ENCODING);
        ByteBuf buf;
        boolean releaseFlag = false;
        if ("br".equalsIgnoreCase(contentEncoding)) {
            ByteBuf byteBuf = httpResponse.content();
            byteBuf.markReaderIndex();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            byteBuf.resetReaderIndex();
            try {
                byte[] decompress = BrotliUtil.decompress(bytes, true);
                buf = Unpooled.wrappedBuffer(decompress);
                releaseFlag = true;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        } else {
            buf = httpResponse.content();
        }
        Content content = contentService.saveContent(buf, request.getUri());
        if (releaseFlag) {
            buf.release();
        }
        Response res = new Response();
        res.setId(UUID.randomUUID().toString());
        res.setRequestId(request.getId());
        res.setContentType(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE));
        res.setHttpVersion(httpResponse.protocolVersion().text());
        res.setStatus(httpResponse.status().code());
        res.setContentId(content.getId());
        res.setStartTime(connectionInfo.getResponseStartTime());
        res.setEndTime(connectionInfo.getResponseEndTime());
        responseMapper.insert(res);
        List<Certificate> serverCertificates = connectionInfo.getServerCertificates();
        certificateInfoService.saveList(serverCertificates, request.getId(), res.getId());
        headerService.saveHeaders(httpResponse.headers(), null, res.getId());
        return res;
    }
}
