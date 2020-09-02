package com.github.supermoonie.certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-09-01
 */
public class BcSelfSignedCertGenerator {

    private static final Provider PROVIDER = new BouncyCastleProvider();

    /**
     * 生成 key
     */
    public static KeyPair genKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", PROVIDER);
        keyPairGenerator.initialize(2048, new SecureRandom());
        return keyPairGenerator.genKeyPair();
    }

    /**
     * 生成ca证书
     */
    public static X509Certificate genCaCert(String subject, Date caNotBefore, Date caNotAfter, KeyPair keyPair) throws Exception {
        X500Name owner = new X500Name(subject);
        JcaX509v3CertificateBuilder jv3Builder =
                new JcaX509v3CertificateBuilder(owner,
                        BigInteger.valueOf(System.currentTimeMillis() + (long)(Math.random() * 10000.0D) + 1000L),
                        caNotBefore, caNotAfter, owner, keyPair.getPublic());
        jv3Builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = jv3Builder.build(signer);
        return new JcaX509CertificateConverter().setProvider(PROVIDER).getCertificate(certHolder);
    }
}
