package com.github.supermoonie.proxy.fx.controller;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/9
 */
public class MainControllerTest {

    @Test
    public void split() throws URISyntaxException {
        String[] arr = "https://www.baidu.com".split("/");
        System.out.println(Arrays.toString(arr));
        URI uri = new URI("https://www.baidu.com/");
        System.out.println(uri.getFragment());
        System.out.println(Arrays.toString(uri.getPath().split("/")));
    }

}