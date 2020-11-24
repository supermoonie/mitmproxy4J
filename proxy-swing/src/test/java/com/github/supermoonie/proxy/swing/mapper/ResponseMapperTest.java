package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.util.Jackson;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class ResponseMapperTest {

    @Test
    public void queryById() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            String id = "182b8a85-84e6-4c58-9e0d-7b4f5a538fd0";
            ResponseMapper mapper = sqlSession.getMapper(ResponseMapper.class);
            Response response = mapper.queryById(id);
            System.out.println(Jackson.toJsonString(response));
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