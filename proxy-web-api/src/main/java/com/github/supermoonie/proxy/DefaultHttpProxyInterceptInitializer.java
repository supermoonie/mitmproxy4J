package com.github.supermoonie.proxy;

import com.github.supermoonie.intercept.HttpProxyInterceptInitializer;
import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import com.github.supermoonie.proxy.intercept.DumpFullRequestIntercept;
import com.github.supermoonie.proxy.intercept.DumpFullResponseIntercept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
@Component("defaultHttpProxyInterceptInitializer")
@Scope("prototype")
@Slf4j
public class DefaultHttpProxyInterceptInitializer implements HttpProxyInterceptInitializer {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void init(HttpProxyInterceptPipeline pipeline) {
        log.debug("HttpProxyInterceptPipeline add {}", DumpFullRequestIntercept.class.getSimpleName());
        pipeline.addLast(applicationContext.getBean("dumpFullRequestIntercept", DumpFullRequestIntercept.class));
        log.debug("HttpProxyInterceptPipeline add {}", DumpFullResponseIntercept.class.getSimpleName());
        pipeline.addLast(applicationContext.getBean("dumpFullResponseIntercept", DumpFullResponseIntercept.class));
    }
}
