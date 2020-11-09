package com.github.supermoonie.proxy.fx.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/9
 */
public class UrlUtilTest {

    @Test
    public void getLastFragment() {
        System.out.println(UrlUtil.getLastFragment("https://www.baidu.com"));
        System.out.println(UrlUtil.getLastFragment("https://www.baidu.com/"));
        System.out.println(UrlUtil.getLastFragment("https://www.baidu.com/abc"));
        System.out.println(UrlUtil.getLastFragment("https://www.baidu.com/abc?foo=bar"));
        System.out.println(UrlUtil.getLastFragment("https://www.baidu.com/abc.jpg"));
    }
}