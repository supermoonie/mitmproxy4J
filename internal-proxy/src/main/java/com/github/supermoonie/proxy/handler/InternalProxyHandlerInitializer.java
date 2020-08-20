package com.github.supermoonie.proxy.handler;

import com.github.supermoonie.proxy.intercept.req.RequestInterceptPipeline;
import com.github.supermoonie.proxy.intercept.res.ResponseInterceptPipeline;

/**
 * @author supermoonie
 * @since 2020/8/20
 */
public interface InternalProxyHandlerInitializer {

    /**
     * initial intercept pipeline
     *
     * @param requestInterceptPipeline  {@link RequestInterceptPipeline}
     * @param responseInterceptPipeline {@link ResponseInterceptPipeline}
     */
    void initInterceptPipeline(RequestInterceptPipeline requestInterceptPipeline,
                               ResponseInterceptPipeline responseInterceptPipeline);
}
