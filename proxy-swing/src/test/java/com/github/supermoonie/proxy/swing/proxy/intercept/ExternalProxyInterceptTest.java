package com.github.supermoonie.proxy.swing.proxy.intercept;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2021/1/25
 */
public class ExternalProxyInterceptTest {

    @Test
    public void test() {
        System.out.println("192.168.1.1".matches("192.168.1.1"));
    }

}