package com.github.supermoonie.controller;

import cn.hutool.core.util.HexUtil;
import org.junit.Test;

/**
 * @author supermoonie
 * @since 2020/6/27
 */
public class DecodeControllerTest {

    @Test(expected = RuntimeException.class)
    public void testHexDecode() {
        System.out.println(HexUtil.decodeHexStr("mitmproxy4J"));
    }
}