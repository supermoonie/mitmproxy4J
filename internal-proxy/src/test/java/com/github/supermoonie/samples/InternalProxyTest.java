package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @author supermoonie
 * @since 2020/8/20
 */
public class InternalProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy();
        proxy.setPort(10801);
        proxy.start();
    }

    private final static TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    public static HttpClient.Builder createTrustAllHttpClientBuilder() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, TRUST_ALL_CERTS, new SecureRandom());
        return HttpClient.newBuilder().sslContext(sslContext);
    }

    @Test
    public void test() throws Exception {
        HttpClient httpClient = createTrustAllHttpClientBuilder().proxy(ProxySelector.of(new InetSocketAddress(10801))).version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("https://httpbin.org/post?foo=bar"));
        builder.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded");
        builder.method("POST", HttpRequest.BodyPublishers.ofByteArray("foo=bar".getBytes(StandardCharsets.UTF_8)));
        HttpResponse<String> res = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        System.out.println(res.body());
    }
}
