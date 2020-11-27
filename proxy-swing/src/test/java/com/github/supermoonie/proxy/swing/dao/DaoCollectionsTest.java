package com.github.supermoonie.proxy.swing.dao;

import com.github.supermoonie.proxy.swing.entity.*;
import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.dao.Dao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        response.setRequestId(1);
        response.setTimeCreated(new Date());
        Dao<Response, Integer> dao = DaoCollections.getDao(Response.class);
        dao.create(response);
        System.out.println(response);
        response = dao.queryForId(response.getId());
        System.out.println(response);
    }

    @Test
    public void test_header_dao() throws SQLException {
        Header header = new Header();
        header.setRequestId(1);
        header.setName("foo");
        header.setValue("bar");
        header.setTimeCreated(new Date());
        Dao<Header, Integer> dao = DaoCollections.getDao(Header.class);
        dao.create(header);
        System.out.println(header);
        header = dao.queryForId(header.getId());
        System.out.println(header);
    }

    @Test
    public void test_content_dao() throws SQLException {
        Content content = new Content();
        content.setRawContent("foo=bar".getBytes(StandardCharsets.UTF_8));
        content.setTimeCreated(new Date());
        Dao<Content, Integer> dao = DaoCollections.getDao(Content.class);
        dao.create(content);
        System.out.println(content);
        content = dao.queryForId(content.getId());
        System.out.println(content);
    }

    @Test
    public void test_certificate_info_dao() throws SQLException {
        CertificateInfo certificateInfo = new CertificateInfo();
        certificateInfo.setSerialNumber("foo");
        certificateInfo.setTimeCreated(new Date());
        Dao<CertificateInfo, Integer> dao = DaoCollections.getDao(CertificateInfo.class);
        dao.create(certificateInfo);
        System.out.println(certificateInfo);
        certificateInfo = dao.queryForId(certificateInfo.getId());
        System.out.println(certificateInfo);
    }

    @Test
    public void test_certificate_ma_dao() throws SQLException {
        CertificateMap certificateMap = new CertificateMap();
        certificateMap.setCertificateSerialNumber("foo");
        certificateMap.setRequestId(1);
        certificateMap.setTimeCreated(new Date());
        Dao<CertificateMap, Integer> dao = DaoCollections.getDao(CertificateMap.class);
        dao.create(certificateMap);
        System.out.println(certificateMap);
        certificateMap = dao.queryForId(certificateMap.getId());
        System.out.println(certificateMap);
    }

    @Test
    public void test_connection_overview_dao() throws SQLException {
        ConnectionOverview connectionOverview = new ConnectionOverview();
        connectionOverview.setRequestId(1);
        connectionOverview.setTimeCreated(new Date());
        Dao<ConnectionOverview, Integer> dao = DaoCollections.getDao(ConnectionOverview.class);
        dao.create(connectionOverview);
        System.out.println(connectionOverview);
        connectionOverview = dao.queryForId(connectionOverview.getId());
        System.out.println(connectionOverview);
    }

}