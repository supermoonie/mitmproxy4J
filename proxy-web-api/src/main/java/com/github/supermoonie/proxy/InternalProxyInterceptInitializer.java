package com.github.supermoonie.proxy;

import com.github.supermoonie.proxy.intercept.LoggingIntercept;
import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author supermoonie
 * @date 2020-09-09
 */
@Component("internalProxyInterceptInitializer")
@Scope("prototype")
@Slf4j
public class InternalProxyInterceptInitializer implements InterceptInitializer {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void initIntercept(Map<String, RequestIntercept> requestIntercepts, Map<String, ResponseIntercept> responseIntercepts) {
        LoggingIntercept loggingIntercept = new LoggingIntercept();
        requestIntercepts.put("log", loggingIntercept);
        responseIntercepts.put("log", loggingIntercept);
    }
}
