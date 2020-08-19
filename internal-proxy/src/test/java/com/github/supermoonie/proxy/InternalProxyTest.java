package com.github.supermoonie.proxy;

import org.junit.Test;

/**
 * @author supermoonie
 * @since 2020/8/20
 */
public class InternalProxyTest {

    @Test
    public void start() throws InterruptedException {
        new InternalProxy(1, 5, 10800).start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
