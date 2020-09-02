//package com.github.supermoonie.certificate;
//
//import sun.security.x509.*;
//
//import java.math.BigInteger;
//import java.security.KeyPair;
//import java.security.PrivateKey;
//import java.security.SecureRandom;
//import java.security.cert.CertificateException;
//import java.util.Date;
//
///**
// * @author supermoonie
// * @since 2020/8/16
// */
//public class OpenJdkSelfSignedCertGenerator {
//
//    static String[] generate(String hosts, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter,
//                             String algorithm) throws Exception {
//        PrivateKey key = keypair.getPrivate();
//
//        // Prepare the information required for generating an X.509 certificate.
//        X509CertInfo info = new X509CertInfo();
//        X500Name owner = new X500Name("CN=" + hosts);
//        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
//        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new BigInteger(64, random)));
//        try {
//            info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
//        } catch (CertificateException ignore) {
//            info.set(X509CertInfo.SUBJECT, owner);
//        }
//        try {
//            info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
//        } catch (CertificateException ignore) {
//            info.set(X509CertInfo.ISSUER, owner);
//        }
//        info.set(X509CertInfo.VALIDITY, new CertificateValidity(notBefore, notAfter));
//        info.set(X509CertInfo.KEY, new CertificateX509Key(keypair.getPublic()));
//        info.set(X509CertInfo.ALGORITHM_ID,
//                new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.sha256WithRSAEncryption_oid)));
//
//        // Sign the cert to identify the algorithm that's used.
//        X509CertImpl cert = new X509CertImpl(info);
//        cert.sign(key, "EC".equalsIgnoreCase(algorithm) ? "SHA256withECDSA" : "SHA256withRSA");
//
//        // Update the algorithm and sign again.
//        info.set(CertificateAlgorithmId.NAME + '.' + CertificateAlgorithmId.ALGORITHM, cert.get(X509CertImpl.SIG_ALG));
//        cert = new X509CertImpl(info);
//        cert.sign(key, "EC".equalsIgnoreCase(algorithm) ? "SHA256withECDSA" : "SHA256withRSA");
//        cert.verify(keypair.getPublic());
//
//        return SelfSignedCertificate.newSelfSignedCertificate(hosts, key, cert);
//    }
//
//    private OpenJdkSelfSignedCertGenerator() {
//    }
//}
