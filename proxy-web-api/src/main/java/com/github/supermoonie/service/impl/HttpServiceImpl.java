package com.github.supermoonie.service.impl;

import com.github.supermoonie.config.MyProxyConfig;
import com.github.supermoonie.runner.NettyProxyRunner;
import com.github.supermoonie.service.HttpService;
import com.github.supermoonie.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @since 2020/7/12
 */
@Service
@Slf4j
public class HttpServiceImpl implements HttpService {

    @Resource
    private MyProxyConfig myProxyConfig;

    @Override
    public CloseableHttpClient getDefaultHttpClient() {
        if (null == NettyProxyRunner.PROXY_SERVER || !NettyProxyRunner.PROXY_SERVER.isListening()) {
            throw new IllegalStateException("Proxy not listening");
        }
        return getDefaultHttpClient(new HttpHost("127.0.0.1", myProxyConfig.getPort()));
    }

    @Override
    public CloseableHttpClient getDefaultHttpClient(HttpHost proxy) {
        HttpClientBuilder httpClientBuilder = HttpClientUtils.createTrustAllHttpClientBuilder();
        return httpClientBuilder.setProxy(proxy).build();
    }
}
