package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.Content;
import com.github.supermoonie.proxy.swing.entity.Header;
import com.github.supermoonie.proxy.swing.util.Jackson;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class HeaderMapperTest {

    @Test
    public void queryById() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            String id = "0070e86b-9fad-4ad2-950d-b2425d39be80";
            HeaderMapper mapper = sqlSession.getMapper(HeaderMapper.class);
            Header header = mapper.queryById(id);
            System.out.println(Jackson.toJsonString(header));
        }
    }

    @Test
    public void queryAllByLimit() {
    }

    @Test
    public void queryAll() {
    }

    @Test
    public void insert() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            HeaderMapper mapper = sqlSession.getMapper(HeaderMapper.class);
            Header header = new Header();
            header.setName("foo");
            header.setValue("bar");
            header.setId("foo");
            header.setRequestId("foo");
            mapper.insert(header);
            sqlSession.commit();
        }
    }

    @Test
    public void update() {
    }

    @Test
    public void deleteById() {
    }
}