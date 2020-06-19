package com.github.supermoonie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Hello world!
 *
 * @author wangc
 */
@SpringBootApplication
@MapperScan(basePackages = "com.github.supermoonie.mapper")
@EnableAsync
public class ProxyWebApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ProxyWebApplication.class, args);
    }
}
