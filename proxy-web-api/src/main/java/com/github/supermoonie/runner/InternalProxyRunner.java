package com.github.supermoonie.runner;

import cn.hutool.json.JSONUtil;
import com.github.supermoonie.config.InternalProxyConfig;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.InternalProxyInterceptInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
@Component
@Slf4j
@Order()
public class InternalProxyRunner implements CommandLineRunner {

    @Resource
    private InternalProxyConfig internalProxyConfig;

    @Resource
    private InternalProxyInterceptInitializer initializer;

    private InternalProxy proxy;

    @Override
    public void run(String... args) throws Exception {
        log.info("InternalProxyConfig: {}", JSONUtil.toJsonStr(internalProxyConfig));
        proxy = new InternalProxy(initializer);
        proxy.setPort(internalProxyConfig.getPort());
        proxy.setBossThreads(new NioEventLoopGroup(internalProxyConfig.getBossThreads()));
        proxy.setWorkerThreads(new NioEventLoopGroup(internalProxyConfig.getWorkerThreads()));
        proxy.setProxyThreads(new NioEventLoopGroup(internalProxyConfig.getProxyThreads()));
        proxy.setUsername(internalProxyConfig.getUsername());
        proxy.setPassword(internalProxyConfig.getPassword());
        proxy.setCaPath(internalProxyConfig.getCaPath());
        proxy.setPrivateKeyPath(internalProxyConfig.getPrivateKeyPath());
        proxy.setSecondProxyConfig(internalProxyConfig.getSecondProxyConfig());
        proxy.setTrafficShaping(false);
        proxy.start();
        GlobalChannelTrafficShapingHandler trafficShapingHandler = proxy.getTrafficShapingHandler();
        TrafficCounter trafficCounter = trafficShapingHandler.trafficCounter();
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                final long totalRead = trafficCounter.cumulativeReadBytes();
//                final long totalWrite = trafficCounter.cumulativeWrittenBytes();
//                System.out.println("total read: " + (totalRead >> 10) + " KB");
//                System.out.println("total write: " + (totalWrite >> 10) + " KB");
                if (proxy.isTrafficShaping()) {
                    log.info(trafficCounter.toString());
                }
            }
        }).start();
    }

    public InternalProxy getProxy() {
        return proxy;
    }

    public void setProxy(InternalProxy proxy) {
        this.proxy = proxy;
    }
}
