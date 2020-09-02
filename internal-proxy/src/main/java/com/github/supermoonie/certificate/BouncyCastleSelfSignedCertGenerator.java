//package com.github.supermoonie.certificate;
//
//import org.bouncycastle.asn1.x500.X500Name;
//import org.bouncycastle.cert.X509CertificateHolder;
//import org.bouncycastle.cert.X509v3CertificateBuilder;
//import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
//import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.operator.ContentSigner;
//import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
//
//import java.math.BigInteger;
//import java.security.KeyPair;
//import java.security.PrivateKey;
//import java.security.Provider;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//import java.util.Date;
//
///**
// * @author supermoonie
// * @since 2020/8/16
// */
//public class BouncyCastleSelfSignedCertGenerator {
//
//    private static final Provider PROVIDER = new BouncyCastleProvider();
//
//    static String[] generate(String hosts, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter,
//                             String algorithm) throws Exception {
//        PrivateKey key = keypair.getPrivate();
//
//        // Prepare the information required for generating an X.509 certificate.
//        X500Name owner = new X500Name("CN=" + hosts);
//        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
//                owner, new BigInteger(64, random), notBefore, notAfter, owner, keypair.getPublic());
//
//        ContentSigner signer = new JcaContentSignerBuilder(
//                "EC".equalsIgnoreCase(algorithm) ? "SHA256withECDSA" : "SHA256WithRSAEncryption").build(key);
//        X509CertificateHolder certHolder = builder.build(signer);
//        X509Certificate cert = new JcaX509CertificateConverter().setProvider(PROVIDER).getCertificate(certHolder);
//        cert.verify(keypair.getPublic());
//
//        return SelfSignedCertificate.newSelfSignedCertificate(hosts, key, cert);
//    }
//
//    private BouncyCastleSelfSignedCertGenerator() {
//    }
//}
