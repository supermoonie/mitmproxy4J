package com.github.supermoonie;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * @author supermoonie
 * @since 2020/6/22
 */
public class HttpProxyTest {

    private CloseableHttpClient httpClient;

    @Before
    public void before() throws Exception {
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .setProxy(new HttpHost("127.0.0.1", 10801))
                .build();
    }

    @Test
    public void test_get() throws Exception {
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://httpbin.org/get?foo=bar"))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_get_m3u8() throws Exception {
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://meinv.jingyu-zuida.com/20200318/13035_bebdd0ec/1000k/hls/index.m3u8"))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://xigua-cdn.haima-zuida.com/20200625/8571_8cc90c6a/1000k/hls/index.m3u8"))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_get_image() throws Exception {
        String url = "https://privacypic.com/images/2020/06/04/ujVMtU.jpg";
//        String url = "https://httpbin.org/image/png";
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_post_form() throws Exception {
//        HttpPost httpPost = new HttpPost("http://127.0.0.1:8866/post");
        HttpPost httpPost = new HttpPost("https://httpbin.org/post?query=string");
        StringEntity entity = new StringEntity("foo=bar", ContentType.create("application/x-www-form-urlencoded", "UTF-8"));
        httpPost.setEntity(entity);
        try(CloseableHttpResponse response = httpClient.execute(httpPost)) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_post_form_data() throws Exception {
//        HttpPost httpPost = new HttpPost("http://127.0.0.1:8866/post");
        HttpPost httpPost = new HttpPost("https://httpbin.org/post?query=string");
        InputStream in = HttpProxyTest.class.getClassLoader().getResourceAsStream("test.jpg");
        assert in != null;
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("foo", "bar")
                .addBinaryBody("test.png", new File("/Users/supermoonie/IdeaProjects/mitmproxy4J/proxy/src/test/resources/test.jpg"))
                .build();
        httpPost.setEntity(httpEntity);
        try(CloseableHttpResponse response = httpClient.execute(httpPost)) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_status_500() throws Exception {
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://httpbin.org/status/500"))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }


}
