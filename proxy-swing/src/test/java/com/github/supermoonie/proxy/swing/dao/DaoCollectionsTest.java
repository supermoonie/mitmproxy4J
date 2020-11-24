package com.github.supermoonie.proxy.swing.dao;

import com.github.supermoonie.proxy.swing.entity.Request;
import com.github.supermoonie.proxy.swing.entity.Response;
import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.dao.Dao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/11/24
 */
public class DaoCollectionsTest {

    @Before
    public void before() throws SQLException {
        DaoCollections.init();
    }

    @After
    public void after() throws IOException {
        DaoCollections.close();
    }

    @Test
    public void test_request_dao() throws SQLException {
        Request request = new Request();
        request.setHost("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
        request.setTimeCreated(new Date());
        DaoCollections.getDao(Request.class).create(request);
        System.out.println(request);
    }

    @Test
    public void test_response_dao() throws SQLException {
        Response response = new Response();
        response.setRequestId("foo");
        response.setTimeCreated(new Date());
        Dao<Response, Integer> dao = DaoCollections.getDao(Response.class);
        dao.create(response);
        System.out.println(response);
        response = dao.queryForId(response.getId());
        System.out.println(response);
    }

}