package com.github.supermoonie.config;

import cn.hutool.system.UserInfo;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        ds.setJdbcUrl("jdbc:sqlite:" + dbPath() + "/proxy.db?date_string_format=yyyy-MM-dd HH:mm:ss&encoding=UTF8");
        return ds;
    }

    private String dbPath() {
        UserInfo userInfo = new UserInfo();
        String homeDir = userInfo.getHomeDir();
        File dbDir = new File(homeDir + File.separator + "db");
        if (!dbDir.exists() && !dbDir.mkdir()) {
            throw new RuntimeException(dbDir.getAbsolutePath() + " create fail!");
        }
        return dbDir.getAbsolutePath();
    }
}
