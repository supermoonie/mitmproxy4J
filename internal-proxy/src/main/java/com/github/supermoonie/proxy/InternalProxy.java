package com.github.supermoonie.proxy;

import com.github.supermoonie.proxy.ex.InternalProxyCloseException;
import com.github.supermoonie.proxy.ex.InternalProxyStartException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-08-08
 */
public class InternalProxy {

    private static final Logger logger = LoggerFactory.getLogger(InternalProxy.class);

    private final GlobalChannelTrafficShapingHandler trafficShapingHandler = new GlobalChannelTrafficShapingHandler(new NioEventLoopGroup(1));

    private static final int DEFAULT_N_BOOS_THREAD = 2;
    private static final int DEFAULT_N_WORKER_THREAD = 16;
    private static final int DEFAULT_N_PROXY_THREAD = 16;
    private static final int DEFAULT_PORT = 10801;
    private static final int DEFAULT_MAX_CONTENT_SIZE = 32 * 1024 * 1024;
    private static final String DEFAULT_CA_FILE_NAME = "ca.crt";
    private static final String DEFAULT_PRIVATE_KEY_FILE_NAME = "ca_private.pem";

    private NioEventLoopGroup bossThreads;
    private NioEventLoopGroup workerThreads;
    private NioEventLoopGroup proxyThreads;
    private int maxContentSize = DEFAULT_MAX_CONTENT_SIZE;
    private int port = DEFAULT_PORT;
    private String username;
    private String password;
    private String caPath;
    private String privateKeyPath;
    private CertificateConfig certificateConfig;
    private volatile boolean trafficShaping = false;
    private SecondProxyConfig secondProxyConfig;
    private final InterceptInitializer initializer;

    public InternalProxy() {
        this.initializer = null;
    }

    public InternalProxy(InterceptInitializer initializer) {
        this.initializer = initializer;
    }

    public void start() {
        ServerBootstrap b = new ServerBootstrap();
        try {
            initialCertificate();
            if (null == bossThreads) {
                bossThreads = new NioEventLoopGroup(DEFAULT_N_BOOS_THREAD);
            }
            if (null == workerThreads) {
                workerThreads = new NioEventLoopGroup(DEFAULT_N_WORKER_THREAD);
            }
            if (null == proxyThreads) {
                proxyThreads = new NioEventLoopGroup(DEFAULT_N_PROXY_THREAD);
            }
            InternalProxy that = this;
            b.group(bossThreads, workerThreads)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("httpCodec", new HttpServerCodec());
                            ch.pipeline().addLast("decompressor", new HttpContentDecompressor());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(maxContentSize));
                            ch.pipeline().addLast("internalProxyHandler", new InternalProxyHandler(that, initializer));
                        }
                    }).bind(port).sync();
            logger.info("proxy listening on {}", port);
        } catch (Exception e) {
            if (null != workerThreads) {
                workerThreads.shutdownGracefully();
            }
            if (null != bossThreads) {
                bossThreads.shutdownGracefully();
            }
            throw new InternalProxyStartException(e);
        }
    }

    public void close() {
        try {
            if (null != proxyThreads) {
                proxyThreads.shutdownGracefully().sync();
                proxyThreads = null;
            }
            if (null != workerThreads) {
                workerThreads.shutdownGracefully().sync();
                workerThreads = null;
            }
            if (null != bossThreads) {
                bossThreads.shutdownGracefully().sync();
                bossThreads = null;
            }
            logger.info("proxy closed on {}", port);
        } catch (InterruptedException e) {
            throw new InternalProxyCloseException(e);
        }

    }

    private void initialCertificate() throws Exception {
        ClassLoader classLoader = InternalProxy.class.getClassLoader();
        X509Certificate caCert;
        if (null == caPath) {
            caCert = CertificateUtil.loadCert(classLoader.getResourceAsStream(DEFAULT_CA_FILE_NAME));
        } else {
            caCert = CertificateUtil.loadCert(caPath);
        }
        PrivateKey caPrivateKey;
        if (null == privateKeyPath) {
            caPrivateKey = CertificateUtil.loadPriKey(classLoader.getResourceAsStream(DEFAULT_PRIVATE_KEY_FILE_NAME));
        } else {
            caPrivateKey = CertificateUtil.loadPriKey(privateKeyPath);
        }
        certificateConfig = new CertificateConfig();
        certificateConfig.clientSslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        certificateConfig.subject = CertificateUtil.getSubject(caCert);
        certificateConfig.caNotBefore = caCert.getNotBefore();
        certificateConfig.caNotAfter = caCert.getNotAfter();
        certificateConfig.caPriKey = caPrivateKey;
        KeyPair keyPair = CertificateUtil.genKeyPair();
        certificateConfig.serverPriKey = keyPair.getPrivate();
        certificateConfig.serverPubKey = keyPair.getPublic();
        logger.debug("load ca {}", caCert);
    }


    public static class SecondProxyConfig {
        private ProxyType proxyType;
        private String host;
        private int port;
        private String username;
        private String password;

        public SecondProxyConfig() {
        }

        public SecondProxyConfig(ProxyType proxyType, String host, int port) {
            this.proxyType = proxyType;
            this.host = host;
            this.port = port;
        }

        public SecondProxyConfig(ProxyType proxyType, String host, int port, String username, String password) {
            this.proxyType = proxyType;
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
        }

        public ProxyType getProxyType() {
            return proxyType;
        }

        public void setProxyType(ProxyType proxyType) {
            this.proxyType = proxyType;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class CertificateConfig {

        private SslContext clientSslCtx;
        private String subject;
        private Date caNotBefore;
        private Date caNotAfter;
        private PrivateKey caPriKey;
        private PrivateKey serverPriKey;
        private PublicKey serverPubKey;

        public SslContext getClientSslCtx() {
            return clientSslCtx;
        }

        public String getSubject() {
            return subject;
        }

        public Date getCaNotBefore() {
            return caNotBefore;
        }

        public Date getCaNotAfter() {
            return caNotAfter;
        }

        public PrivateKey getCaPriKey() {
            return caPriKey;
        }

        public PrivateKey getServerPriKey() {
            return serverPriKey;
        }

        public PublicKey getServerPubKey() {
            return serverPubKey;
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NioEventLoopGroup getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(NioEventLoopGroup bossThreads) {
        this.bossThreads = bossThreads;
    }

    public NioEventLoopGroup getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(NioEventLoopGroup workerThreads) {
        this.workerThreads = workerThreads;
    }

    public NioEventLoopGroup getProxyThreads() {
        return proxyThreads;
    }

    public void setProxyThreads(NioEventLoopGroup proxyThreads) {
        this.proxyThreads = proxyThreads;
    }

    public String getCaPath() {
        return caPath;
    }

    public void setCaPath(String caPath) {
        this.caPath = caPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public CertificateConfig getCertificateConfig() {
        return certificateConfig;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SecondProxyConfig getSecondProxyConfig() {
        return secondProxyConfig;
    }

    public void setSecondProxyConfig(SecondProxyConfig secondProxyConfig) {
        this.secondProxyConfig = secondProxyConfig;
    }

    public int getMaxContentSize() {
        return maxContentSize;
    }

    public void setMaxContentSize(int maxContentSize) {
        this.maxContentSize = maxContentSize;
    }

    public boolean isTrafficShaping() {
        return trafficShaping;
    }

    public void setTrafficShaping(boolean trafficShaping) {
        this.trafficShaping = trafficShaping;
    }

    public GlobalChannelTrafficShapingHandler getTrafficShapingHandler() {
        return trafficShapingHandler;
    }
}
