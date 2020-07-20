package com.github.supermoonie.service;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author supermoonie
 * @since 2020/7/8
 */
public class FlowServiceTester {

    @Test
    public void test_url() throws MalformedURLException {
        URL url = new URL("https://oauthaccountmanager.googleapis.com/v1/issuetoken");
        System.out.println(url.getProtocol());
        System.out.println(url.getHost());
        System.out.println(url.getPort());
        System.out.println(url.getDefaultPort());
        System.out.println(url.getPath());
        System.out.println(url.getQuery());
        System.out.println(url.getRef());
        System.out.println("------------------------");
        System.out.println(url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort()));
        System.out.println(Arrays.toString(url.getPath().split("/")));
        System.out.println(url.getPath().split("/")[url.getPath().split("/").length -1] + "?" + url.getQuery());
        System.out.println("------------------------");
        System.out.println(new URL("https://clients1.google.com").getPath());
        System.out.println(Arrays.toString("".split("/")));
        System.out.println(Arrays.toString("foo/bar/".split("/")));
        System.out.println("------------------------");
        System.out.println(new URL("https://httpbin.org/get?foo=bar&").getQuery());
    }
}
