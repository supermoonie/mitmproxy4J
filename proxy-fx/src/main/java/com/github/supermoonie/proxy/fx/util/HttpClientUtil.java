package com.github.supermoonie.proxy.fx.util;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * @author supermoonie
 * @since 2020/11/8
 */
public class HttpClientUtil {

    private HttpClientUtil() {

    }

    public static HttpClientBuilder createTrustAllApacheHttpClientBuilder() throws Exception {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, (chain, authType) -> true);
        SSLConnectionSocketFactory sslsf = new
                SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        return HttpClients.custom().setSSLSocketFactory(sslsf);
    }
}
