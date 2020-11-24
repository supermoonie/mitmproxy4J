package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.CertificateInfo;
import com.github.supermoonie.proxy.swing.util.Jackson;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class CertificateInfoMapperTest {


    @Test
    public void queryById() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
            String id = "ff613260-f720-4808-9cbd-e674e7beb1a9";
            CertificateInfoMapper mapper = sqlSession.getMapper(CertificateInfoMapper.class);
            CertificateInfo certificateInfo = mapper.queryById(id);
            System.out.println(Jackson.toJsonString(certificateInfo));
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