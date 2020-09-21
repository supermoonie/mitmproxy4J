package com.github.supermoonie.proxy.util;

import com.github.supermoonie.proxy.CertificateUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author supermoonie
 * @since 2020/9/20
 */
public final class CertInstallUtils {

    private static final Pattern WINDOWS_CERT_HASH_PATTERN = Pattern.compile("(?i)\\(sha1\\):\\s(.*)\r?\n");

    private static final Pattern MACOS_CERT_HASH_PATTERN = Pattern.compile("(?i)SHA-1 hash:\\s(.*)\r?\n");

    public static final String INTERNAL_PROXY_HOME = SystemPropertyUtil.get("user.home") + File.separator + ".internal_proxy/";

    private CertInstallUtils() {
    }

    /**
     * 拷贝证书到代理家目录
     */
    public static Path copyCertToProxyHome(InputStream in) throws IOException {
        Path proxyHome = Paths.get(INTERNAL_PROXY_HOME);
        if (!Files.exists(proxyHome)) {
            Files.createDirectory(proxyHome);
        }
        Path path = Paths.get(proxyHome.toString(), "ca.cert");
        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        return path;
    }

    /**
     * 安装证书
     */
    public static void install(InputStream in) throws IOException {
        Path path = copyCertToProxyHome(in);
        ExecUtils.execBlockWithAdmin("security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain " + path.toString());
    }

    /**
     * 卸载证书
     */
    public static void uninstall(String commonName) throws IOException {
        String certList = listCert(commonName);
        if (null == certList) {
            return;
        }
        if (PlatformDependent.isWindows()) {
            Matcher matcher = WINDOWS_CERT_HASH_PATTERN.matcher(certList);
            while (matcher.find()) {
                String hash = matcher.group(1).replaceAll("\\s", "");
                ExecUtils.execBlock("certutil",
                        "-delstore",
                        "-user",
                        "root",
                        hash);
            }
        } else if (PlatformDependent.isOsx()) {
            Matcher matcher = MACOS_CERT_HASH_PATTERN.matcher(certList);
            while (matcher.find()) {
                String hash = matcher.group(1);
                ExecUtils.execBlockWithAdmin("security delete-identity -Z " + hash);
            }
        }
    }

    /**
     * 检测证书是否已经安装
     */
    public static boolean isCertInstalled(InputStream in) throws Exception {
        if (null == in) {
            return false;
        }
        X509Certificate cert = CertificateUtil.loadCert(in);
        String subjectName = CertificateUtil.getSubject(cert);
        String cn = CertificateUtil.getCN(subjectName).orElse("");
        String sha1 = CertificateUtil.getCertSHA1(cert);
        String certList = CertInstallUtils.listCert(cn);
        if (null == certList) {
            return false;
        }
        certList = certList.toUpperCase();
        return Arrays.stream(certList.split("\\s")).anyMatch(line -> line.contains(sha1));
    }

    /**
     * 查询证书列表
     * https://ss64.com/osx/security-find-cert.html
     */
    public static String listCert(String subjectName) throws IOException {
        if (PlatformDependent.isWindows()) {
            return ExecUtils.exec("certutil ",
                    "-store",
                    "-user",
                    "root",
                    subjectName);
        } else if (PlatformDependent.isOsx()) {
            return ExecUtils.exec("security",
                    "find-certificate",
                    "-a",
                    "-c",
                    subjectName,
                    "-p",
                    "-Z");
        }
        return null;
    }
}
