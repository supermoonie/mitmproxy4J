package com.github.supermoonie.proxy;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2021/1/27
 */
public class ProxyTypeTest {

    @Test
    public void t() {
        System.out.println(ProxyType.valueOf("HTTP").getCode());
    }

}