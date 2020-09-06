package com.github.supermoonie.proxy;

import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;

import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/8/20
 */
public interface InterceptInitializer {

    /**
     * initial intercept pipeline
     *
     * @param requestIntercepts  {@link RequestIntercept}
     * @param responseIntercepts {@link ResponseIntercept}
     */
    void initIntercept(Map<String, RequestIntercept> requestIntercepts,
                       Map<String, ResponseIntercept> responseIntercepts);
}
