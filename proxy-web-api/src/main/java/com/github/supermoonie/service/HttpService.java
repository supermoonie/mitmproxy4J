package com.github.supermoonie.service;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/7/12
 */
public interface HttpService {

    /**
     * 获取默认配置的带有本地代理的HttpClient
     *
     * @return {@link CloseableHttpClient}
     */
    CloseableHttpClient getDefaultHttpClient();

    /**
     * 获取指定代理的默认配置的HttpClient
     *
     * @param proxy 代理 {@link HttpHost}
     * @return {@link CloseableHttpClient}
     */
    CloseableHttpClient getDefaultHttpClient(HttpHost proxy);
}
