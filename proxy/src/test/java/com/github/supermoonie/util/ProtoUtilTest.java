package com.github.supermoonie.util;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @date 2020-06-12
 */
public class ProtoUtilTest {

    private static final Pattern BASE_URI_PATTERN = Pattern.compile("^(?<base>https?://[^/]*/).*$");

    @Test
    public void test_pattern() {
        Matcher matcher = BASE_URI_PATTERN.matcher("http://httpbin.org/get");
        if (matcher.find()) {
            System.out.println(matcher.group("base"));
        }
        Matcher matcher_2 = BASE_URI_PATTERN.matcher("https://httpbin.org/get");
        if (matcher_2.find()) {
            System.out.println(matcher_2.group("base"));
        }
    }

}