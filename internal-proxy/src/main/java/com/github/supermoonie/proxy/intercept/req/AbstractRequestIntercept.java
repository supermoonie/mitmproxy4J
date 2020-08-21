package com.github.supermoonie.proxy.intercept.req;

import com.github.supermoonie.proxy.intercept.InterceptContext;
import com.github.supermoonie.util.ResponseUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public abstract class AbstractRequestIntercept implements RequestIntercept {

    AbstractRequestIntercept pre;

    AbstractRequestIntercept next;

    @Override
    public FullHttpResponse onException(InterceptContext ctx, FullHttpRequest request, Exception ex) throws Exception {
        return ResponseUtils.htmlResponse(ex.getMessage(), HttpResponseStatus.OK);
    }
}
