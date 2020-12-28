package com.github.supermoonie.runner;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.config.InternalProxyConfig;
import com.github.supermoonie.constant.EnumConfigType;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.proxy.DefaultConfigIntercept;
import com.github.supermoonie.proxy.DefaultRemoteMapIntercept;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.InternalProxyInterceptInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private DefaultConfigIntercept defaultConfigIntercept;

    @Resource
    private DefaultRemoteMapIntercept defaultRemoteMapIntercept;
    
    private InternalProxy proxy;

    @Override
    public void run(String... args) throws Exception {
        initConfigIntercept();
        start();
    }

    private void initConfigIntercept() {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", EnumConfigType.REMOTE_URI_MAP).orderByDesc("timeCreated");
        List<Config> configs = configMapper.selectList(queryWrapper);
        Map<String, String> remoteUriMap = defaultRemoteMapIntercept.getRemoteUriMap();
        remoteUriMap.putAll(configs.stream().filter(config -> !StringUtils.isEmpty(config.getKey()) && !StringUtils.isEmpty(config.getValue())).collect(Collectors.toMap(Config::getKey, Config::getValue)));
    }

    public void restart(int port) {
        if (null != proxy) {
            internalProxyConfig.setPort(port);
            proxy.close();
            start();
        }
    }

    private void start() {
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
