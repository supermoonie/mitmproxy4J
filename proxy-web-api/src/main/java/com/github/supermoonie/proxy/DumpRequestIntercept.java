package com.github.supermoonie.proxy;

import com.github.supermoonie.model.Request;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.service.RequestService;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
@Component("dumpFullRequestIntercept")
@Scope("prototype")
@Slf4j
public class DumpRequestIntercept implements RequestIntercept {

    @Resource
    private RequestService requestService;

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        Request req = requestService.saveRequest(request);
        return null;
    }

    @Override
    public FullHttpResponse onException(InterceptContext ctx, HttpRequest request, Throwable cause) {
        return null;
    }
}
