package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.ConnectionOverview;
import com.github.supermoonie.proxy.swing.util.Jackson;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public class ConnectionOverviewMapperTest {

    @Test
    public void queryById() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            String id = "foo";
            ConnectionOverviewMapper mapper = sqlSession.getMapper(ConnectionOverviewMapper.class);
            ConnectionOverview connectionOverview = mapper.queryById(id);
            System.out.println(Jackson.toJsonString(connectionOverview));
        }
    }

    @Test
    public void insert() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            ConnectionOverviewMapper mapper = sqlSession.getMapper(ConnectionOverviewMapper.class);
            ConnectionOverview connectionOverview = new ConnectionOverview();
            connectionOverview.setId("foo");
            mapper.insert(connectionOverview);
            sqlSession.commit();
        }
    }
}