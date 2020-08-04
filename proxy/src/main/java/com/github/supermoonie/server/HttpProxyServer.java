package com.github.supermoonie.server;

import com.github.supermoonie.crt.CertPool;
import com.github.supermoonie.crt.CertUtil;
import com.github.supermoonie.exception.HttpProxyExceptionHandle;
import com.github.supermoonie.handler.HttpProxyServerHandle;
import com.github.supermoonie.intercept.HttpProxyInterceptInitializer;
import com.github.supermoonie.intercept.HttpTunnelIntercept;
import com.github.supermoonie.proxy.SecondProxyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author wangc
 */
public class HttpProxyServer {

    private final Logger log = LoggerFactory.getLogger(HttpProxyServer.class);

    private HttpProxyCACertFactory caCertFactory;
    private HttpProxyServerConfig serverConfig;
    private HttpProxyInterceptInitializer proxyInterceptInitializer;
    private HttpTunnelIntercept tunnelIntercept;
    private HttpProxyExceptionHandle httpProxyExceptionHandle;
    private SecondProxyConfig secondProxyConfig;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private volatile boolean listening = false;

    private void init() {
        if (serverConfig == null) {
            serverConfig = new HttpProxyServerConfig();
        }
        serverConfig.setProxyLoopGroup(new NioEventLoopGroup(serverConfig.getProxyGroupThreads()));
        if (serverConfig.isHandleSsl()) {
            try {
                serverConfig.setClientSslCtx(
                        SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                                .build());
                ClassLoader classLoader = HttpProxyServer.class.getClassLoader();
                X509Certificate caCert;
                PrivateKey caPriKey;
                if (caCertFactory == null) {
                    caCert = CertUtil.loadCert(classLoader.getResourceAsStream("ca.crt"));
                    caPriKey = CertUtil.loadPriKey(classLoader.getResourceAsStream("ca_private.pem"));
                } else {
                    caCert = caCertFactory.getCACert();
                    caPriKey = caCertFactory.getCAPriKey();
                }
                //读取CA证书使用者信息
                serverConfig.setIssuer(CertUtil.getSubject(caCert));
                //读取CA证书有效时段(server证书有效期超出CA证书的，在手机上会提示证书不安全)
                serverConfig.setCaNotBefore(caCert.getNotBefore());
                serverConfig.setCaNotAfter(caCert.getNotAfter());
                //CA私钥用于给动态生成的网站SSL证书签证
                serverConfig.setCaPriKey(caPriKey);
                //生产一对随机公私钥用于网站SSL证书动态创建
                KeyPair keyPair = CertUtil.genKeyPair();
                serverConfig.setServerPriKey(keyPair.getPrivate());
                serverConfig.setServerPubKey(keyPair.getPublic());
            } catch (Exception e) {
                log.error(String.format("error: %s, serverConfig.setHandleSsl(false)", e.getMessage()), e);
                serverConfig.setHandleSsl(false);
            }
        }
        if (proxyInterceptInitializer == null) {
            proxyInterceptInitializer = pipeline -> {
            };
        }
        if (httpProxyExceptionHandle == null) {
            httpProxyExceptionHandle = new HttpProxyExceptionHandle();
        }
    }

    public HttpProxyServer serverConfig(HttpProxyServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public HttpProxyServer proxyInterceptInitializer(
            HttpProxyInterceptInitializer proxyInterceptInitializer) {
        this.proxyInterceptInitializer = proxyInterceptInitializer;
        return this;
    }

    public HttpProxyServer httpProxyExceptionHandle(
            HttpProxyExceptionHandle httpProxyExceptionHandle) {
        this.httpProxyExceptionHandle = httpProxyExceptionHandle;
        return this;
    }

    public HttpProxyServer proxyConfig(SecondProxyConfig secondProxyConfig) {
        this.secondProxyConfig = secondProxyConfig;
        return this;
    }

    public HttpProxyServer caCertFactory(HttpProxyCACertFactory caCertFactory) {
        this.caCertFactory = caCertFactory;
        return this;
    }

    public HttpProxyServer tunnelIntercept(HttpTunnelIntercept tunnelIntercept) {
        this.tunnelIntercept = tunnelIntercept;
        return this;
    }

    public synchronized void start(int port) {
        if (listening) {
            log.warn("mitmproxy4J is listening on " + port);
            return;
        }
        init();
        bossGroup = new NioEventLoopGroup(serverConfig.getBossGroupThreads());
        workerGroup = new NioEventLoopGroup(serverConfig.getWorkerGroupThreads());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast("httpCodec", new HttpServerCodec());
                            ch.pipeline().addLast("serverHandle",
                                    new HttpProxyServerHandle(serverConfig,
                                            proxyInterceptInitializer,
                                            tunnelIntercept,
                                            secondProxyConfig,
                                            httpProxyExceptionHandle));
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            log.info("mitmproxy4J start listening on {}", port);
            listening = true;
//            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public synchronized void close() {
        if (listening) {
            serverConfig.getProxyLoopGroup().shutdownGracefully();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            listening = false;
        }
    }

    public boolean isListening() {
        return listening;
    }
}
