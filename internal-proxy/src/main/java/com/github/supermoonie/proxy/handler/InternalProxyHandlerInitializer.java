package com.github.supermoonie.proxy.handler;

import com.github.supermoonie.proxy.intercept.InterceptPipeline;

/**
 * @author supermoonie
 * @since 2020/8/20
 */
public interface InternalProxyHandlerInitializer {

    /**
     * initial intercept pipeline
     *
     * @param pipeline  {@link InterceptPipeline}
     */
    void initInterceptPipeline(InterceptPipeline pipeline);
}
