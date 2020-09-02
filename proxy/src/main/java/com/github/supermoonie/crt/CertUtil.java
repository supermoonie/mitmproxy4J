package com.github.supermoonie.crt;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * CertUtil
 *
 * @author wangc
 */
public class CertUtil {

    private static KeyFactory keyFactory = null;

    static {
        //注册BouncyCastleProvider加密库
        Security.addProvider(new BouncyCastleProvider());
    }

    private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
        if (keyFactory == null) {
            keyFactory = KeyFactory.getInstance("RSA");
        }
        return keyFactory;
    }

    /**
     * 生成RSA公私密钥对,长度为2048
     */
    public static KeyPair genKeyPair() throws Exception {
        KeyPairGenerator caKeyPairGen = KeyPairGenerator.getInstance("RSA", "BC");
        caKeyPairGen.initialize(2048, new SecureRandom());
        return caKeyPairGen.genKeyPair();
    }

    /**
     * 从文件加载RSA私钥 openssl pkcs8 -topk8 -nocrypt -inform PEM -outform DER -in ca.key -out
     * ca_private.der
     */
    public static PrivateKey loadPriKey(byte[] bts)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bts);
        return getKeyFactory().generatePrivate(privateKeySpec);
    }

    /**
     * 从文件加载RSA私钥 openssl pkcs8 -topk8 -nocrypt -inform PEM -outform DER -in ca.key -out
     * ca_private.der
     */
    public static PrivateKey loadPriKey(String path) throws Exception {
        return loadPriKey(Files.readAllBytes(Paths.get(path)));
    }

    /**
     * 从文件加载RSA私钥 openssl pkcs8 -topk8 -nocrypt -inform PEM -outform DER -in ca.key -out
     * ca_private.der
     */
    public static PrivateKey loadPriKey(URI uri) throws Exception {
        return loadPriKey(Paths.get(uri).toString());
    }

    /**
     * 从文件加载RSA私钥 openssl pkcs8 -topk8 -nocrypt -inform PEM -outform DER -in ca.key -out
     * ca_private.der
     */
    public static PrivateKey loadPriKey(InputStream inputStream)
            throws Exception {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return loadPriKey(bytes);
    }

    /**
     * 从文件加载RSA公钥 openssl rsa -in ca.key -pubout -outform DER -out ca_pub.der
     */
    public static PublicKey loadPubKey(byte[] bts) throws Exception {
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bts);
        return getKeyFactory().generatePublic(publicKeySpec);
    }

    /**
     * 从文件加载RSA公钥 openssl rsa -in ca.key -pubout -outform DER -out ca_pub.der
     */
    public static PublicKey loadPubKey(String path) throws Exception {
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Files.readAllBytes(Paths.get(path)));
        return getKeyFactory().generatePublic(publicKeySpec);
    }

    /**
     * 从文件加载RSA公钥 openssl rsa -in ca.key -pubout -outform DER -out ca_pub.der
     */
    public static PublicKey loadPubKey(URI uri) throws Exception {
        return loadPubKey(Paths.get(uri).toString());
    }

    /**
     * 从文件加载RSA公钥 openssl rsa -in ca.key -pubout -outform DER -out ca_pub.der
     */
    public static PublicKey loadPubKey(InputStream inputStream) throws Exception {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return loadPubKey(bytes);
    }

    /**
     * 从文件加载证书
     */
    public static X509Certificate loadCert(InputStream inputStream) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(inputStream);
    }

    /**
     * 从文件加载证书
     */
    public static X509Certificate loadCert(String path) throws Exception {
        return loadCert(new FileInputStream(path));
    }

    /**
     * 从文件加载证书
     */
    public static X509Certificate loadCert(URI uri) throws Exception {
        return loadCert(Paths.get(uri).toString());
    }

    /**
     * 读取ssl证书使用者信息
     */
    public static String getSubject(InputStream inputStream) throws Exception {
        X509Certificate certificate = loadCert(inputStream);
        return getSubject(certificate);
    }

    /**
     * 读取ssl证书使用者信息
     */
    public static String getSubject(X509Certificate certificate) {
        //读出来顺序是反的需要反转下
        List<String> tempList = Arrays.asList(certificate.getIssuerDN().toString().split(", "));
        return IntStream.rangeClosed(0, tempList.size() - 1)
                .mapToObj(i -> tempList.get(tempList.size() - i - 1)).collect(Collectors.joining(", "));
    }

    /**
     * 动态生成服务器证书,并进行CA签授
     *
     * @param issuer 颁发机构
     */
    public static X509Certificate genCert(String issuer, PrivateKey caPriKey, Date caNotBefore,
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
                BigInteger.valueOf(System.currentTimeMillis() + (long) (Math.random() * 10000) + 1000),
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

    private static final Provider PROVIDER = new BouncyCastleProvider();

    /**
     * 生成CA服务器证书
     */
    public static X509Certificate genCaCert(String subject, Date caNotBefore, Date caNotAfter,
                                            KeyPair keyPair) throws Exception {

        // Prepare the information required for generating an X.509 certificate.
        X500Name owner = new X500Name(subject);
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                owner, new BigInteger(64, new SecureRandom()), caNotBefore, caNotAfter, owner, keyPair.getPublic());

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keyPair.getPrivate());
        X509CertificateHolder certHolder = builder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider(PROVIDER).getCertificate(certHolder);
        cert.verify(keyPair.getPublic());
        return cert;
//        JcaX509v3CertificateBuilder jv3Builder = new JcaX509v3CertificateBuilder(new X500Name(subject),
//                BigInteger.valueOf(System.currentTimeMillis() + (long) (Math.random() * 10000) + 1000),
//                caNotBefore,
//                caNotAfter,
//                new X500Name(subject),
//                keyPair.getPublic());
//        jv3Builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));
//        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
//                .build(keyPair.getPrivate());
//        return new JcaX509CertificateConverter().getCertificate(jv3Builder.build(signer));
    }

    public static boolean savePemX509Certificate(X509Certificate cert, PrivateKey key, Writer writer) throws Exception
    {
        if(cert!=null && key!=null && writer!=null)
        {
            JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
            pemWriter.writeObject(cert);
            pemWriter.flush();
            pemWriter.writeObject(key);
            pemWriter.flush();
            pemWriter.close();
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//        keyGen.initialize(2048, new SecureRandom());
//        KeyPair keypair = keyGen.generateKeyPair();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        X509Certificate certificate = CertUtil.genCaCert("CN=InternalProxy", new Date(System.currentTimeMillis() - 1000000), dateFormat.parse("2020-12-01 00:00:00"), keypair);
//        System.out.println(X509Factory.BEGIN_CERT);
//        System.out.println(Base64.getEncoder().encodeToString(certificate.getEncoded()));
//        System.out.println(X509Factory.END_CERT);
//        ByteBuf wrappedBuf = Unpooled.wrappedBuffer(certificate.getEncoded());
//        ByteBuf encodedBuf = io.netty.handler.codec.base64.Base64.encode(wrappedBuf, true);
//        String content = X509Factory.BEGIN_CERT + "\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n" + X509Factory.END_CERT + "\n";
//        try (OutputStream certOut = new FileOutputStream(new File("/Users/moonie/Desktop/test1.crt"))) {
//            certOut.write(content.getBytes(CharsetUtil.US_ASCII));
//        }
//        IOUtils.write(content.getBytes(StandardCharsets.US_ASCII), new FileOutputStream(new File("/Users/moonie/Desktop/test.crt")));
    }
}
