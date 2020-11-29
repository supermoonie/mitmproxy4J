package com.github.supermoonie.proxy.fx.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.JDBC;

import javax.sql.DataSource;
import java.io.File;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
@Configuration
public class DbConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(org.sqlite.JDBC.class.getName());
        ds.setJdbcUrl(JDBC.PREFIX + dbPath() + "/internal_proxy2.db?date_string_format=yyyy-MM-dd HH:mm:ss&encoding=UTF8");
        return ds;
    }

    private String dbPath() {
        String homeDir = System.getProperty("user.home");
        File dbDir = new File(homeDir + File.separator + ".proxy_fx");
        if (!dbDir.exists() && !dbDir.mkdir()) {
            throw new RuntimeException(dbDir.getAbsolutePath() + " create fail!");
        }
        return dbDir.getAbsolutePath();
    }
}
