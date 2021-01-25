package com.github.supermoonie.config;

import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.SecondProxyConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
@Configuration
@EnableConfigurationProperties(InternalProxyConfig.class)
@ConfigurationProperties(prefix = "internal-proxy", ignoreInvalidFields = true)
@Validated
public class InternalProxyConfig {

    @Min(1000)
    private Integer port = 10801;

    @Min(1)
    private Integer bossThreads;

    @Min(5)
    private Integer workerThreads;

    @Min(5)
    private Integer proxyThreads;

    private String username;

    private String password;

    private String caPath;

    private String privateKeyPath;

    private SecondProxyConfig secondProxyConfig;


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

    @Override
    public String toString() {
        return "InternalProxyConfig{" +
                "port=" + port +
                ", bossThreads=" + bossThreads +
                ", workerThreads=" + workerThreads +
                ", proxyThreads=" + proxyThreads +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", caPath='" + caPath + '\'' +
                ", privateKeyPath='" + privateKeyPath + '\'' +
                ", secondProxyConfig=" + secondProxyConfig +
                '}';
    }
}
