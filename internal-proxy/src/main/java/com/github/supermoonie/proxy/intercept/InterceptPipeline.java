package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.intercept.req.RequestIntercept;
import com.github.supermoonie.proxy.intercept.res.ResponseIntercept;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public interface InterceptPipeline<T> {

    /**
     * add first
     *
     * @param t {@link RequestIntercept} or {@link ResponseIntercept}
     */
    void addFirst(T t);

    /**
     * add last
     *
     * @param t {@link RequestIntercept} or {@link ResponseIntercept}
     */
    void addLast(T t);
}
