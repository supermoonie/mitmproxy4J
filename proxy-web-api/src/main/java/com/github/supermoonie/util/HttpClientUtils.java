package com.github.supermoonie.util;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/7/14
 */
public class HttpClientUtils {

    public static <T> void executeWithoutEntity(HttpClientBuilder clientBuilder, HttpMethod method, String url, Map<String, Object> query, List<BasicHeader> headers, ResponseHandler<T> responseHandler) throws IOException {
        url = addQuery(url, query);
        HttpUriRequest request;
        if (method.equals("GET")) {
            request = new HttpGet(url);
        } else if (method.equals("HEAD")) {
            request = new HttpHead(url);
        } else {
            throw new IllegalArgumentException("Method " + method + " not support!");
        }
        try (CloseableHttpClient httpClient = clientBuilder.build()) {
            for (BasicHeader header : headers) {
                request.addHeader(header);
            }
            httpClient.execute(request, responseHandler);
        }
    }

    public static <T> void get(HttpClientBuilder clientBuilder, String url, List<BasicHeader> headers, ResponseHandler<T> responseHandler) throws IOException {
        get(clientBuilder, url, null, headers, responseHandler);
    }

    public static <T> void get(HttpClientBuilder clientBuilder, String url, Map<String, Object> query, List<BasicHeader> headers, ResponseHandler<T> responseHandler) throws IOException {
        url = addQuery(url, query);
        try (CloseableHttpClient httpClient = clientBuilder.build()) {
            HttpGet get = new HttpGet(url);
            for (BasicHeader header : headers) {
                get.addHeader(header);
            }
            httpClient.execute(get, responseHandler);
        }
    }

    public static String addQuery(String url, Map<String, Object> query) {
        if (null != query && query.size() > 0) {
            StringBuilder queryStringBuilder = new StringBuilder();
            query.forEach((name, value) -> queryStringBuilder.append(name).append("=").append(value).append("&"));
            queryStringBuilder.deleteCharAt(queryStringBuilder.length() - 1);
            int index = url.indexOf("?");
            if (index != -1) {
                if (url.endsWith("&")) {
                    url = url + queryStringBuilder.toString();
                } else {
                    url = url + '&' + queryStringBuilder.toString();
                }
            } else {
                url = url + '?' + queryStringBuilder.toString();
            }
        }
        return url;
    }

    public static HttpClientBuilder createTrustAllHttpClientBuilder() {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, (chain, authType) -> true);
            SSLConnectionSocketFactory factory = new
                    SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
