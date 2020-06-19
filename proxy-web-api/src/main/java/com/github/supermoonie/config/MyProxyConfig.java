package com.github.supermoonie.config;

import com.github.supermoonie.proxy.ProxyType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
@Configuration
@EnableConfigurationProperties(MyProxyConfig.class)
@ConfigurationProperties(prefix = MyProxyConfig.PREFIX, ignoreInvalidFields = true)
public class MyProxyConfig {

    static final String PREFIX = "proxy";

    private Integer port = 10801;

    private Integer bossGroupThreads = 1;

    private Integer workerGroupThreads = 5;

    private Integer proxyGroupThreads = 5;

    private Boolean handleSsl = true;

    private SecondProxy secondProxy;

    public static class SecondProxy {
        private ProxyType type;

        private String host;

        private Integer port;

        private String user;

        private String password;

        public ProxyType getType() {
            return type;
        }

        public void setType(ProxyType type) {
            this.type = type;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "SecondProxy{" +
                    "type=" + type +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    ", user='" + user + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getBossGroupThreads() {
        return bossGroupThreads;
    }

    public void setBossGroupThreads(Integer bossGroupThreads) {
        this.bossGroupThreads = bossGroupThreads;
    }

    public Integer getWorkerGroupThreads() {
        return workerGroupThreads;
    }

    public void setWorkerGroupThreads(Integer workerGroupThreads) {
        this.workerGroupThreads = workerGroupThreads;
    }

    public Integer getProxyGroupThreads() {
        return proxyGroupThreads;
    }

    public void setProxyGroupThreads(Integer proxyGroupThreads) {
        this.proxyGroupThreads = proxyGroupThreads;
    }

    public Boolean getHandleSsl() {
        return handleSsl;
    }

    public void setHandleSsl(Boolean handleSsl) {
        this.handleSsl = handleSsl;
    }

    public SecondProxy getSecondProxy() {
        return secondProxy;
    }

    public void setSecondProxy(SecondProxy secondProxy) {
        this.secondProxy = secondProxy;
    }

    @Override
    public String toString() {
        return "MyProxyConfig{" +
                "port=" + port +
                ", bossGroupThreads=" + bossGroupThreads +
                ", workerGroupThreads=" + workerGroupThreads +
                ", proxyGroupThreads=" + proxyGroupThreads +
                ", handleSsl=" + handleSsl +
                ", secondProxy=" + secondProxy +
                '}';
    }
}
