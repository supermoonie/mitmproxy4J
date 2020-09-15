package com.github.supermoonie.proxy;

import com.github.supermoonie.model.Request;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.service.RequestService;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
@Component
@Slf4j
public class DumpHttpRequestIntercept implements RequestIntercept {

    @Resource
    private RequestService requestService;

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        Request req = requestService.saveRequest(request);
        ctx.setUserData(req);
        log.info("{} saved", req.getUri());
        return null;
    }
}
