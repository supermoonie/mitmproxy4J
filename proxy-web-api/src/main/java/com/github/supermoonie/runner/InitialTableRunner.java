package com.github.supermoonie.runner;

import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
@Component
@Slf4j
@Order(0)
public class InitialTableRunner implements CommandLineRunner {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        ClassPathResource sqlResource = new ClassPathResource("crate_table.sql");
        String initSql = sqlResource.readUtf8Str();
        String[] sqlArr = initSql.split("--EOF--");
        for (String sql : sqlArr) {
            log.debug("create table sql:\n{}", sql);
            jdbcTemplate.execute(sql);
        }
    }
}
