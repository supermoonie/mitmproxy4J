package com.github.supermoonie.config;

import com.github.supermoonie.proxy.InternalProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
@Configuration
@EnableConfigurationProperties(InternalProxyConfig.class)
@ConfigurationProperties(prefix = "internal-proxy", ignoreInvalidFields = true)
public class InternalProxyConfig {

    private Integer port;

    private Integer bossThreads;

    private Integer workerThreads;

    private Integer proxyThreads;

    private SecondProxyConfig secondProxyConfig;

    public static class SecondProxyConfig extends InternalProxy.SecondProxyConfig {

    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(Integer bossThreads) {
        this.bossThreads = bossThreads;
    }

    public Integer getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(Integer workerThreads) {
        this.workerThreads = workerThreads;
    }

    public Integer getProxyThreads() {
        return proxyThreads;
    }

    public void setProxyThreads(Integer proxyThreads) {
        this.proxyThreads = proxyThreads;
    }

    public SecondProxyConfig getSecondProxyConfig() {
        return secondProxyConfig;
    }

    public void setSecondProxyConfig(SecondProxyConfig secondProxyConfig) {
        this.secondProxyConfig = secondProxyConfig;
    }

    @Override
    public String toString() {
        return "InternalProxyConfig{" +
                "port=" + port +
                ", bossThreads=" + bossThreads +
                ", workerThreads=" + workerThreads +
                ", proxyThreads=" + proxyThreads +
                ", secondProxyConfig=" + secondProxyConfig +
                '}';
    }
}
