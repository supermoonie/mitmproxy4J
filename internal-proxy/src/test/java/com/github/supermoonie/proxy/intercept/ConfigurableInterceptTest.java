package com.github.supermoonie.proxy.intercept;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/12/27
 */
public class ConfigurableInterceptTest {

    @Test
    public void test_uri() throws URISyntaxException {
        String uri = "https://example.com/foo/bar";
        String path = new URI(uri).getPath();
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        System.out.println(fileName);
    }

}