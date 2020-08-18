package com.github.supermoonie.proxy.intercept;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public interface InterceptPipeline extends Intercept {

    /**
     * add first
     *
     * @param intercept {@link AbstractIntercept}
     * @return  {@link InterceptPipeline}
     */
    InterceptPipeline addFirst(AbstractIntercept intercept);

    /**
     * add last
     *
     * @param intercept {@link AbstractIntercept}
     * @return  {@link InterceptPipeline}
     */
    InterceptPipeline addLast(AbstractIntercept intercept);
}
