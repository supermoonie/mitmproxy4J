package com.github.supermoonie.proxy.swing.db;

import com.github.supermoonie.proxy.swing.mapper.ConnectionOverviewMapper;
import com.github.supermoonie.proxy.swing.mapper.ContentMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;
import java.io.File;

/**
 * @author supermoonie
 * @date 2020-11-22
 */
public class Db {

    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        SQL_SESSION_FACTORY = init();
    }

    public static SqlSessionFactory sqlSessionFactory() {
        return SQL_SESSION_FACTORY;
    }

    private static DataSource defaultDataSource() {
        SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
        SQLiteConfig config = new SQLiteConfig();
        config.setDateClass(SQLiteConfig.DateClass.REAL.getValue());
        config.enableShortColumnNames(true);
        config.setBusyTimeout(3_000);
        dataSource.setConfig(config);
        dataSource.setDatabaseName("internal_proxy");
        dataSource.setUrl(JDBC.PREFIX + dbPath() + "/internal_proxy.db?date_string_format=yyyy-MM-dd HH:mm:ss&encoding=UTF8");
        return dataSource;
    }

    private static SqlSessionFactory init() {
        DataSource dataSource = defaultDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("p", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(ContentMapper.class);
        configuration.addMapper(ConnectionOverviewMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private static String dbPath() {
        String homeDir = System.getProperty("user.home");
        File dbDir = new File(homeDir + File.separator + ".proxy_fx");
        if (!dbDir.exists() && !dbDir.mkdir()) {
            throw new RuntimeException(dbDir.getAbsolutePath() + " create fail!");
        }
        return dbDir.getAbsolutePath();
    }
}
