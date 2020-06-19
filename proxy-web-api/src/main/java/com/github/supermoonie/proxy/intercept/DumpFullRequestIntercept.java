package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import com.github.supermoonie.intercept.common.BaseFullReqIntercept;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.service.RequestService;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-06-09
 */
@Component("dumpFullRequestIntercept")
@Scope("prototype")
@Slf4j
public class DumpFullRequestIntercept extends BaseFullReqIntercept {

    @Resource
    private RequestService requestService;

    @Override
    public boolean match(HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
        return true;
    }

    @Override
    public void handelRequest(FullHttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
        log.debug("{} received uri: {}", httpRequest.getClass().getSimpleName(), httpRequest.uri());
        Request request = requestService.saveRequest(httpRequest);
        pipeline.setRequest(request);
    }
}
