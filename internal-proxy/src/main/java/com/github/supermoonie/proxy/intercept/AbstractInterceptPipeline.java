package com.github.supermoonie.proxy.intercept;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public abstract class AbstractInterceptPipeline<T> implements InterceptPipeline<T> {

    protected T head;

    protected T tail;

}
