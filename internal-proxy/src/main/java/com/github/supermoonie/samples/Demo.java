package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.handler.InternalProxyHandlerInitializer;
import com.github.supermoonie.proxy.intercept.InterceptContext;
import com.github.supermoonie.proxy.intercept.req.AbstractRequestIntercept;
import com.github.supermoonie.proxy.intercept.req.RequestInterceptPipeline;
import com.github.supermoonie.proxy.intercept.res.ResponseInterceptPipeline;
import com.github.supermoonie.util.ResponseUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author supermoonie
 * @since 2020/8/21
 */
public class Demo {

    public static void main(String[] args) {
        new InternalProxy(1, 5, 10800, new InternalProxyHandlerInitializer() {
            @Override
            public void initInterceptPipeline(RequestInterceptPipeline requestInterceptPipeline, ResponseInterceptPipeline responseInterceptPipeline) {
                requestInterceptPipeline.addFirst(new AbstractRequestIntercept() {
                    @Override
                    public FullHttpResponse onRequest(InterceptContext ctx, FullHttpRequest request) {
//                        request.headers().add("foo", "bar");
                        return ResponseUtils.htmlResponse("Intercept by mitmproxy4J", HttpResponseStatus.OK);
                    }
                });
            }
        }).start();
    }
}
