package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.ConnectionOverview;
import com.github.supermoonie.proxy.swing.entity.Content;
import com.github.supermoonie.proxy.swing.util.Jackson;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class ContentMapperTest {

    @Test
    public void queryById() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            String id = "c1cb2569-e3a1-4149-9f43-1c9b99732ccd";
            ContentMapper mapper = sqlSession.getMapper(ContentMapper.class);
            Content content = mapper.queryById(id);
            System.out.println(Jackson.toJsonString(content));
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
            ContentMapper mapper = sqlSession.getMapper(ContentMapper.class);
            Content content = new Content();
            content.setContent(new byte[]{1, 2, 3});
            content.setId("foo");
            mapper.insert(content);
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