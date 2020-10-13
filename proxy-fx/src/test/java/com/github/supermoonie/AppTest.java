package com.github.supermoonie;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        URL url = new URL("https://httpbin.org/get?foo=bar");
        System.out.println(url.getQuery());
        System.out.println(url.getPath());
        System.out.println(url.getProtocol());
        System.out.println(url.getDefaultPort());
        System.out.println(url.getRef());
        System.out.println(url.getPort());
        System.out.println(url.getHost());
        System.out.println(url.getAuthority());
        System.out.println(url.getContent());
        System.out.println(url.getFile());
        System.out.println(url.getUserInfo());
    }

    @Test
    public void testUri() throws URISyntaxException {
        URI url = new URI("https://abc@192.168.1.1:9999/get/ip?foo=bar");
        System.out.println(url.getAuthority());
        System.out.println(url.getFragment());
        System.out.println(url.getHost());
        System.out.println(url.getPath());
        System.out.println(Arrays.toString((url.getPath() + " ").split("/")));
        System.out.println(url.getPort());
        System.out.println(url.getQuery());
        System.out.println(url.getRawAuthority());
        System.out.println(url.getRawFragment());
        System.out.println(url.getRawPath());
        System.out.println(url.getRawQuery());
        System.out.println(url.getRawSchemeSpecificPart());
        System.out.println(url.getRawUserInfo());
        System.out.println(url.getScheme());
        System.out.println(url.getSchemeSpecificPart());
        System.out.println(url.getUserInfo());
    }

    @Test
    public void testSplit() {
        String text = "foo=";
        System.out.println(Arrays.toString(text.split("=")));
    }
}
