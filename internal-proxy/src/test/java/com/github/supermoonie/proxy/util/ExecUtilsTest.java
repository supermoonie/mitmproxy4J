package com.github.supermoonie.proxy.util;

import org.junit.Test;

import java.io.IOException;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class ExecUtilsTest {

    @Test
    public void execBlockWithAdmin() throws IOException {
        ExecUtils.execBlockWithAdmin("security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain /Users/supermoonie/IdeaProjects/mitmproxy4J/internal-proxy/src/test/resources/ca.crt");
    }

}