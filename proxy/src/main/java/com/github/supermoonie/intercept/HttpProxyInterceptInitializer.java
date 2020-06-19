package com.github.supermoonie.intercept;

/**
 * 初始化代理监听器
 *
 * @author wangc
 */
@FunctionalInterface
public interface HttpProxyInterceptInitializer {

    /**
     * init
     *
     * @param pipeline {@link HttpProxyInterceptPipeline}
     */
    void init(HttpProxyInterceptPipeline pipeline);
}
