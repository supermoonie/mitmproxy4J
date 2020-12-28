package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.InterceptInitializer;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;

import java.util.Map;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
public class InternalProxyInterceptInitializer implements InterceptInitializer {

    @Override
    public void initIntercept(Map<String, RequestIntercept> requestIntercepts, Map<String, ResponseIntercept> responseIntercepts) {
        requestIntercepts.put("accessControlRequestIntercept", AccessControlRequestIntercept.INSTANCE);
        requestIntercepts.put("configurableIntercept", DefaultConfigIntercept.INSTANCE);
        requestIntercepts.put("dumpHttpRequestIntercept", DumpHttpRequestIntercept.INSTANCE);
        requestIntercepts.put("defaultLocalMapIntercept", DefaultLocalMapIntercept.INSTANCE);
        responseIntercepts.put("dumpHttpResponseIntercept", DumpHttpResponseIntercept.INSTANCE);
    }
}
