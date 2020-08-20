package com.github.supermoonie.proxy.intercept.res;

import com.github.supermoonie.proxy.intercept.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public class LastResponseIntercept extends AbstractResponseIntercept {

    @Override
    public boolean onResponse(InterceptContext ctx, FullHttpResponse response) {
        ctx.getClientChannel().writeAndFlush(response);
        return true;
    }

    @Override
    public boolean onException(InterceptContext ctx, FullHttpResponse response, Exception ex) throws Exception {
        return true;
    }
}
