package com.github.supermoonie.controller;

import cn.hutool.core.util.HexUtil;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author supermoonie
 * @date 2020-07-23
 */
public class FlowControllerTest {

    @Test
    public void test_hex() {
        System.out.println(HexUtil.decodeHexStr("666f6f3d626172"));
    }

}