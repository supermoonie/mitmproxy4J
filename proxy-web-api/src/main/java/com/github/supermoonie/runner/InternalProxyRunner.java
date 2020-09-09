package com.github.supermoonie.runner;

import com.github.supermoonie.config.InternalProxyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Override
    public void run(String... args) throws Exception {

    }
}
