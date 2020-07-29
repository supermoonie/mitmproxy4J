package com.github.supermoonie;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.*;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/6/22
 */
public class HttpProxyTest {

    private CloseableHttpClient httpClient;

    @Before
    public void before() throws Exception {
        httpClient = createTrustAllHttpClientBuilder()
                .setProxy(new HttpHost("127.0.0.1", 10801))
                .build();
    }

    public static HttpClientBuilder createTrustAllHttpClientBuilder() throws Exception {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, (chain, authType) -> true);
        SSLConnectionSocketFactory sslsf = new
                SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        return HttpClients.custom().setSSLSocketFactory(sslsf);
    }

    @Test
    public void test_all() {
        HttpGet get = new HttpGet();
        HttpOptions options = new HttpOptions();
        HttpTrace trace = new HttpTrace();
        HttpHead head = new HttpHead();
        HttpDelete delete = new HttpDelete();
        HttpPut put = new HttpPut();
        put.setEntity(null);
        HttpPost post = new HttpPost();
        post.setEntity(null);
        HttpPatch patch = new HttpPatch();
        patch.setEntity(null);
    }

    @Test
    public void test_contentType_parse() {
        System.out.println(ContentType.parse("application/json; charset=utf8").getMimeType());
    }

    @Test
    public void test_get() throws Exception {
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://httpbin.org/get?foo=bar"))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_delete() throws Exception {
        HttpDelete httpDelete = new HttpDelete("https://httpbin.org/delete?foo=bar");
        try(CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_put() throws Exception {
        HttpPut httpPut = new HttpPut("https://httpbin.org/put");
        StringEntity entity = new StringEntity("foo=bar");
        httpPut.setEntity(entity);
        try(CloseableHttpResponse response = httpClient.execute(httpPut)) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_options() throws Exception {
        HttpOptions httpOptions = new HttpOptions("https://httpbin.org/options");
        try(CloseableHttpResponse response = httpClient.execute(httpOptions)) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_get_loop() throws Exception {
        for (int i = 0; i < 100; i ++) {
            try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://httpbin.org/get?index=" + i))) {
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
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
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_get_video() throws Exception {
        String url = "https://baiducdncnnmsla.weilekangnet.com:59666/data5/08D01D34EDF5EC26/B8F999719875362E/360p/0out360p49.ts";
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_get_html() throws Exception {
        String url = "https://www.myac601dt1sowwn.xyz:59980/index.php/vod/search/page/3/wd/swag.html";
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_post_image() throws Exception {
        String url = "https://httpbin.org/post";
        HttpPost httpPost = new HttpPost(url);
        byte[] bytes = FileUtils.readFileToByteArray(new File("/Users/supermoonie/IdeaProjects/mitmproxy4J/proxy/src/test/resources/test.jpg"));
        httpPost.setEntity(new ByteArrayEntity(bytes, ContentType.IMAGE_JPEG));
        try(CloseableHttpResponse response = httpClient.execute(httpPost)) {
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

    @Test(expected = UnsupportedCharsetException.class)
    public void test_contentType() {
        ContentType.TEXT_PLAIN.withCharset("error-charset");
    }

    @Test
    public void test_post_form_data() throws Exception {
//        HttpPost httpPost = new HttpPost("http://127.0.0.1:8866/post");
        HttpPost httpPost = new HttpPost("https://httpbin.org/post?query=string");
        InputStream in = HttpProxyTest.class.getClassLoader().getResourceAsStream("test.jpg");
        assert in != null;
        byte[] bytes = FileUtils.readFileToByteArray(new File("/Users/supermoonie/IdeaProjects/mitmproxy4J/proxy/src/test/resources/test.jpg"));
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("foo", "bar", ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8))
                .addBinaryBody("test\"abc.png", bytes, ContentType.APPLICATION_OCTET_STREAM, "test.jpg")
                .build();
        httpPost.setEntity(httpEntity);
        try(CloseableHttpResponse response = httpClient.execute(httpPost)) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_status_5xx() throws Exception {
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://httpbin.org/status/500"))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_status_3xx() throws Exception {
        try(CloseableHttpResponse response = httpClient.execute(new HttpGet("https://httpbin.org/status/304"))) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    public void test_requestBuilder() throws Exception {
        RequestBuilder builder = RequestBuilder.get("https://httpbin.org/get");
        try(CloseableHttpResponse response = httpClient.execute(builder.build())) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        }
    }


}
