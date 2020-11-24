package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.CertificateInfo;
import com.github.supermoonie.proxy.swing.entity.CertificateMap;
import com.github.supermoonie.proxy.swing.util.Jackson;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class CertificateMapMapperTest {

    @Test
    public void queryById() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            String id = "254fa861-34d6-419e-82af-745844a6bed9";
            CertificateMapMapper mapper = sqlSession.getMapper(CertificateMapMapper.class);
            CertificateMap map = mapper.queryById(id);
            System.out.println(Jackson.toJsonString(map));
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