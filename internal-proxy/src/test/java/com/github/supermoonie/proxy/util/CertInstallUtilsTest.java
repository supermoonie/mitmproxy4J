package com.github.supermoonie.proxy.util;

import com.github.supermoonie.proxy.CertificateUtil;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public class CertInstallUtilsTest {

    @Test
    public void listCert() throws Exception {
        InputStream in = CertInstallUtilsTest.class.getClassLoader().getResourceAsStream("ca.crt");
        X509Certificate cert = CertificateUtil.loadCert(in);
        String subjectName = CertificateUtil.getSubject(cert);
        String cn = CertificateUtil.getCN(subjectName).orElse("");
        System.out.println(cn);
        String sha1 = CertificateUtil.getCertSHA1(cert);
        System.out.println(sha1);
        String sha256 = CertificateUtil.getCertSHA256(cert);
        System.out.println(sha256);
        String list = CertInstallUtils.listCert(cn);
        System.out.println(list);
    }

    @Test
    public void isCertInstalled() throws Exception {
        InputStream in = CertInstallUtilsTest.class.getClassLoader().getResourceAsStream("ca.crt");
        boolean certInstalled = CertInstallUtils.isCertInstalled(in);
        System.out.println(certInstalled);
    }

    @Test
    public void copyCertToUserHome() throws IOException {
        InputStream in = CertInstallUtilsTest.class.getClassLoader().getResourceAsStream("ca.crt");
        CertInstallUtils.copyCertToProxyHome(in);
    }

    @Test
    public void install() throws Exception {
        try (InputStream in = CertInstallUtilsTest.class.getClassLoader().getResourceAsStream("ca.crt")) {
            boolean certInstalled = CertInstallUtils.isCertInstalled(in);
            if (!certInstalled) {
                try (InputStream inputStream = CertInstallUtilsTest.class.getClassLoader().getResourceAsStream("ca.crt")) {
                    CertInstallUtils.install(inputStream);
                }
            }
        }
    }

    @Test
    public void uninstall() throws Exception {
        try (InputStream in = CertInstallUtilsTest.class.getClassLoader().getResourceAsStream("ca.crt")) {
            boolean certInstalled = CertInstallUtils.isCertInstalled(in);
            if (certInstalled) {
                try (InputStream inputStream = CertInstallUtilsTest.class.getClassLoader().getResourceAsStream("ca.crt")) {
                    X509Certificate cert = CertificateUtil.loadCert(inputStream);
                    String subjectName = CertificateUtil.getSubject(cert);
                    String cn = CertificateUtil.getCN(subjectName).orElse("");
                    CertInstallUtils.uninstall(cn);
                }
            }
        }
    }

}