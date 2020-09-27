package com.github.supermoonie.proxy;

import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
@Component
@Slf4j
public class InternalProxyInterceptInitializer implements InterceptInitializer {

    @Resource
    private DumpHttpRequestIntercept dumpHttpRequestIntercept;

    @Resource
    private DumpHttpResponseIntercept dumpHttpResponseIntercept;

    @Resource
    private DefaultConfigIntercept defaultConfigIntercept;

    @Override
    public void initIntercept(Map<String, RequestIntercept> requestIntercepts, Map<String, ResponseIntercept> responseIntercepts) {
        requestIntercepts.put("dumpHttpRequestIntercept", dumpHttpRequestIntercept);
        responseIntercepts.put("dumpHttpResponseIntercept", dumpHttpResponseIntercept);
        requestIntercepts.put("configurableIntercept", defaultConfigIntercept);
    }
}
