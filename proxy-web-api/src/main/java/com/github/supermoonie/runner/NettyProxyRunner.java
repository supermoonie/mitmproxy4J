package com.github.supermoonie.runner;

import cn.hutool.json.JSONUtil;
import com.github.supermoonie.config.MyProxyConfig;
import com.github.supermoonie.proxy.DefaultHttpProxyExceptionHandle;
import com.github.supermoonie.proxy.DefaultHttpProxyInterceptInitializer;
import com.github.supermoonie.proxy.SecondProxyConfig;
import com.github.supermoonie.server.HttpProxyServer;
import com.github.supermoonie.server.HttpProxyServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
@Component
@Slf4j
@Order()
public class NettyProxyRunner implements CommandLineRunner {

    @Resource
    private MyProxyConfig myProxyConfig;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private DefaultHttpProxyExceptionHandle defaultHttpProxyExceptionHandle;

    @Override
    public void run(String... args) {
        log.debug("MyProxyConfig: {}", JSONUtil.toJsonStr(myProxyConfig));
        HttpProxyServerConfig serverConfig = proxyServerConfig();
        SecondProxyConfig proxyConfig = proxyConfig();
        HttpProxyServer proxyServer = new HttpProxyServer()
                .proxyConfig(proxyConfig)
                .serverConfig(serverConfig)
                .proxyInterceptInitializer(applicationContext.getBean("defaultHttpProxyInterceptInitializer", DefaultHttpProxyInterceptInitializer.class))
                .httpProxyExceptionHandle(defaultHttpProxyExceptionHandle);
        proxyServer.start(myProxyConfig.getPort());
    }

    private HttpProxyServerConfig proxyServerConfig() {
        HttpProxyServerConfig serverConfig = new HttpProxyServerConfig();
        serverConfig.setHandleSsl(myProxyConfig.getHandleSsl());
        serverConfig.setBossGroupThreads(myProxyConfig.getBossGroupThreads());
        serverConfig.setWorkerGroupThreads(myProxyConfig.getWorkerGroupThreads());
        serverConfig.setProxyGroupThreads(myProxyConfig.getProxyGroupThreads());
        return serverConfig;
    }

    private SecondProxyConfig proxyConfig() {
        MyProxyConfig.SecondProxy proxyConfig = myProxyConfig.getSecondProxy();
        if (null == proxyConfig) {
            return null;
        }
        boolean flag = null == proxyConfig.getType()
                || StringUtils.isEmpty(proxyConfig.getHost())
                || proxyConfig.getPort() <= 0;
        if (flag) {
            return null;
        }
        SecondProxyConfig secondProxyConfig = new SecondProxyConfig();
        secondProxyConfig.setHost(proxyConfig.getHost());
        secondProxyConfig.setPort(proxyConfig.getPort());
        secondProxyConfig.setProxyType(proxyConfig.getType());
        secondProxyConfig.setUser(proxyConfig.getUser());
        secondProxyConfig.setPwd(proxyConfig.getPassword());
        return secondProxyConfig;
    }
}
