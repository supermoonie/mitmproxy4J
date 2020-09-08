package com.github.supermoonie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
@Configuration
@EnableConfigurationProperties(MyProxyConfig.class)
@ConfigurationProperties(prefix = "internal-proxy", ignoreInvalidFields = true)
public class InternalProxyConfig {

    private Integer port;

    private Integer nBossThreads;

    private Integer nWorkerThreads;



}
