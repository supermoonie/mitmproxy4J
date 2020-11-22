package com.github.supermoonie.proxy.swing.db;

import com.github.supermoonie.proxy.swing.entity.Content;
import com.github.supermoonie.proxy.swing.mapper.ContentMapper;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @date 2020-11-22
 */
public class DbTest {

    @Test
    public void test_db() {
        try (SqlSession sqlSession = Db.sqlSessionFactory().openSession()) {
//            sqlSession.select("select id from content where id = '276c72ea-55bd-42fc-9d0a-fe8852d0a4e5'", new ResultHandler() {
//                @Override
//                public void handleResult(ResultContext resultContext) {
//                    System.out.println(resultContext.toString());
//                }
//            });
            Statement statement = sqlSession.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from content where id = '276c72ea-55bd-42fc-9d0a-fe8852d0a4e5'");
            while (resultSet.next()) {
//                Timestamp time_created = resultSet.getTimestamp("time_created");
                System.out.println(resultSet.getString("time_created"));
            }
            ContentMapper mapper = sqlSession.getMapper(ContentMapper.class);
            Content content = mapper.selectById("276c72ea-55bd-42fc-9d0a-fe8852d0a4e5");
            System.out.println(content);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}