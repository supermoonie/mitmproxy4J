package com.github.supermoonie.certificate;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @date 2020-09-01
 */
public class BcSelfSignedCertGeneratorTest {

    private byte[] getPrivateKeyContent(PrivateKey privateKey) throws Exception {
        PemObject pemObject = new PemObject("PRIVATE KEY", privateKey.getEncoded());
        StringWriter stringWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        return stringWriter.toString().getBytes();
    }

    @Test
    public void genKeyPair() throws Exception {
        KeyPair keyPair = BcSelfSignedCertGenerator.genKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        FileUtils.writeByteArrayToFile(new File("C:\\Users\\wangc\\Desktop\\temp\\private.der"), privateKey.getEncoded());
        byte[] privateKeyContent = getPrivateKeyContent(privateKey);
        FileUtils.writeByteArrayToFile(new File("C:\\Users\\wangc\\Desktop\\temp\\private.pem"), privateKeyContent);
        ByteBuf wrappedBuf = Unpooled.wrappedBuffer(privateKey.getEncoded());
        ByteBuf encodedBuf = Base64.encode(wrappedBuf, true);
        try {
            String keyText = "-----BEGIN PRIVATE KEY-----\n" +
                    encodedBuf.toString(CharsetUtil.US_ASCII) +
                    "\n-----END PRIVATE KEY-----\n";

            FileUtils.write(new File("C:\\Users\\wangc\\Desktop\\temp\\private.key"), keyText, StandardCharsets.UTF_8);
        } finally {
            encodedBuf.release();
            wrappedBuf.release();
        }
        byte[] cert = BcSelfSignedCertGenerator.genCaCert(
                "C=CN, ST=Shanghai, L=Shanghai, O=github, OU=supermoonie, CN=mitmproxy4J",
                new Date(),
                new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3650)),
                keyPair).getEncoded();
        FileUtils.writeByteArrayToFile(new File("C:\\Users\\wangc\\Desktop\\temp\\private.crt"), cert);
    }
}