package com.github.supermoonie.controller;

import org.junit.Test;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author supermoonie
 * @since 2020/7/19
 */
public class HttpControllerTest {

    @Test
    public void test_parse_mediaType() {
        String disposition = "Content-Disposition: form-data; name=\"files\"; filename=\"test.jpg\"";
        System.out.println(disposition.matches("Content-Disposition:\\s+form-data;\\s+name=.*;\\s+filename=.*"));
    }

}