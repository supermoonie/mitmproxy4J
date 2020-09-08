package com.github.supermoonie.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
@Component
@Slf4j
@Order()
public class InternalProxyRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

    }
}
