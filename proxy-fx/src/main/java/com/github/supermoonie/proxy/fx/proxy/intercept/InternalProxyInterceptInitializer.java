package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.github.supermoonie.proxy.InterceptInitializer;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;

import java.util.Map;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
public class InternalProxyInterceptInitializer implements InterceptInitializer {

    public static final InternalProxyInterceptInitializer INSTANCE = new InternalProxyInterceptInitializer();

    private InternalProxyInterceptInitializer() {

    }

    private DumpHttpRequestIntercept dumpHttpRequestIntercept;

    private DumpHttpResponseIntercept dumpHttpResponseIntercept;

    private DefaultConfigIntercept defaultConfigIntercept;

    @Override
    public void initIntercept(Map<String, RequestIntercept> requestIntercepts, Map<String, ResponseIntercept> responseIntercepts) {
        requestIntercepts.put("dumpHttpRequestIntercept", dumpHttpRequestIntercept);
        responseIntercepts.put("dumpHttpResponseIntercept", dumpHttpResponseIntercept);
        requestIntercepts.put("configurableIntercept", defaultConfigIntercept);
    }
}
