package com.github.supermoonie.samples;

import com.github.supermoonie.proxy.InternalProxy;

/**
 * @author supermoonie
 * @since 2020/8/20
 */
public class InternalProxyTest {

    public static void main(String[] args) {
        InternalProxy proxy = new InternalProxy();
        proxy.setPort(10801);
        proxy.start();
        proxy.close();
    }
}
