package com.github.supermoonie.certificate;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author supermoonie
 * @date 2020-09-01
 */
public class BouncyCastleSelfSignedCertGeneratorTest {

    @Test
    public void genKeyPair() throws Exception {
        KeyPair keyPair = BouncyCastleSelfSignedCertGenerator.genKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        FileUtils.writeByteArrayToFile(new File("/Users/supermoonie/Desktop/temp/private.pem"), privateKey.getEncoded());
        byte[] cert = BouncyCastleSelfSignedCertGenerator.genCaCert(
                "C=CN, ST=Shanghai, L=Shanghai, O=github, OU=supermoonie, CN=mitmproxy4J",
                new Date(),
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)),
                keyPair).getEncoded();
        ByteBuf wrappedBuf = Unpooled.wrappedBuffer(cert);
        ByteBuf encodedBuf = Base64.encode(wrappedBuf, true);
        try {
            String keyText = "-----BEGIN CERTIFICATE-----\n" +
                    encodedBuf.toString(CharsetUtil.US_ASCII) +
                    "\n-----END CERTIFICATE-----\n";
            FileUtils.write(new File("/Users/supermoonie/Desktop/temp/private.crt"), keyText, StandardCharsets.UTF_8);
        } finally {
            encodedBuf.release();
            wrappedBuf.release();
        }

    }
}