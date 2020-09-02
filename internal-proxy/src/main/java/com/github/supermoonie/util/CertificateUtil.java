package com.github.supermoonie.util;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author supermoonie
 * @since 2020/8/17
 */
public class CertificateUtil {

    public static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";

    public static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    private static final Map<Integer, Map<String, X509Certificate>> CERT_CACHE = new WeakHashMap<>();

    private static PrivateKey serverPrivateKey;

    private static PublicKey serverPublicKey;

    private static X509Certificate ca;

    private static PrivateKey caPrivateKey;

    private static SslContext clientSslContext;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator serverKeyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            serverKeyPairGenerator.initialize(2048, new SecureRandom());
            KeyPair serverKeyPair = serverKeyPairGenerator.generateKeyPair();
            serverPrivateKey = serverKeyPair.getPrivate();
            serverPublicKey = serverKeyPair.getPublic();
            clientSslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CertificateUtil() {
    }

    public static SslContext getClientSslContext() {
        return clientSslContext;
    }

    public static PrivateKey getServerPrivateKey() {
        return serverPrivateKey;
    }

    public static PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public static X509Certificate loadCa(String fileName) throws CertificateException {
        if (null != ca) {
            return ca;
        }
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ca = (X509Certificate) cf.generateCertificate(CertificateUtil.class.getClassLoader().getResourceAsStream(fileName));
        return ca;
    }

    public static PrivateKey loadCaPrivateKey(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (null != caPrivateKey) {
            return caPrivateKey;
        }
        InputStream resourceAsStream = CertificateUtil.class.getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = IOUtils.readFully(resourceAsStream, resourceAsStream.available());
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytes);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        caPrivateKey = kf.generatePrivate(privateKeySpec);
        return caPrivateKey;
    }

//    public static PrivateKey loadCaPrivateKey(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        if (null != caPrivateKey) {
//            return caPrivateKey;
//        }
//        String content = String.join("", IOUtils.readLines(Objects.requireNonNull(CertificateUtil.class.getClassLoader().getResourceAsStream(fileName)), "UTF-8"));
//        System.out.println(content);
//        content = content.replace("-----BEGIN PRIVATE KEY-----", "")
//                .replace("-----END PRIVATE KEY-----", "")
//                .replaceAll("\\s", "");
//        System.out.println(content);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content));
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        caPrivateKey = kf.generatePrivate(spec);
//        return caPrivateKey;
//    }

    public static X509Certificate getCert(Integer port, String host, X509Certificate ca, PrivateKey privateKey)
            throws Exception {
        X509Certificate cert = null;
        if (host != null) {
            Map<String, X509Certificate> portCertCache = CERT_CACHE.computeIfAbsent(port, k -> new HashMap<>(5));
            String key = host.trim().toLowerCase();
            if (portCertCache.containsKey(key)) {
                return portCertCache.get(key);
            } else {
                cert = genCert(ca.getIssuerDN().toString(), privateKey,
                        ca.getNotBefore(), ca.getNotAfter(), serverPublicKey, key);
                portCertCache.put(key, cert);
            }
        }
        return cert;
    }

    public static void clear() {
        CERT_CACHE.clear();
    }

    /**
     * 动态生成服务器证书,并进行CA签授
     *
     * @param issuer 颁发机构
     */
    private static X509Certificate genCert(String issuer, PrivateKey caPriKey, Date caNotBefore,
                                           Date caNotAfter, PublicKey serverPubKey,
                                           String... hosts) throws Exception {
        /* String issuer = "C=CN, ST=GD, L=SZ, O=lee, OU=study, CN=ProxyeeRoot";
        String subject = "C=CN, ST=GD, L=SZ, O=lee, OU=study, CN=" + host;*/
        //根据CA证书subject来动态生成目标服务器证书的issuer和subject
        final String commonName = "CN";
        String subject = Stream.of(issuer.split(", ")).map(item -> {
            String[] arr = item.split("=");
            if (commonName.equals(arr[0])) {
                return "CN=" + hosts[0];
            } else {
                return item;
            }
        }).collect(Collectors.joining(", "));

        //doc from https://www.cryptoworkshop.com/guide/
        JcaX509v3CertificateBuilder jv3Builder = new JcaX509v3CertificateBuilder(new X500Name(issuer),
                //issue#3 修复ElementaryOS上证书不安全问题(serialNumber为1时证书会提示不安全)，避免serialNumber冲突，采用时间戳+4位随机数生成
                BigInteger.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10L) + 1000L),
                caNotBefore,
                caNotAfter,
                new X500Name(subject),
                serverPubKey);
        //SAN扩展证书支持的域名，否则浏览器提示证书不安全
        GeneralName[] generalNames = new GeneralName[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            generalNames[i] = new GeneralName(GeneralName.dNSName, hosts[i]);
        }
        GeneralNames subjectAltName = new GeneralNames(generalNames);
        jv3Builder.addExtension(Extension.subjectAlternativeName, false, subjectAltName);
        //SHA256 用SHA1浏览器可能会提示证书不安全
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(caPriKey);
        return new JcaX509CertificateConverter().getCertificate(jv3Builder.build(signer));
    }
}
