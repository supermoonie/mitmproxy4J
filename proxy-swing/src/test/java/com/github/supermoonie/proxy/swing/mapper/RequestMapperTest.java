package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.Header;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.util.Jackson;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class RequestMapperTest {

    @Test
    public void queryById() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            String id = "cecbde3a-1cf1-4aae-b001-c8325d961546";
            RequestMapper mapper = sqlSession.getMapper(RequestMapper.class);
            Request request = mapper.queryById(id);
            System.out.println(Jackson.toJsonString(request));
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
    }

    @Test
    public void update() {
    }

    @Test
    public void deleteById() {
    }
}