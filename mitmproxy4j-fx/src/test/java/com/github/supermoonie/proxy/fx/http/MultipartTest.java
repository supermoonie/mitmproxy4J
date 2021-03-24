package com.github.supermoonie.proxy.fx.http;

import com.github.supermoonie.proxy.fx.AppPreferences;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.dao.FlowDao;
import com.github.supermoonie.proxy.fx.entity.Content;
import com.github.supermoonie.proxy.fx.ui.Flow;
import com.j256.ormlite.dao.Dao;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2021/3/24
 */
public class MultipartTest {

    @Before
    public void before() throws SQLException {
        AppPreferences.init("/mitmproxy4j");
        DaoCollections.init();
    }

    @Test
    public void parse() throws SQLException, IOException {
        Flow flow = FlowDao.getFlow(666);
        Dao<Content, Integer> contentDao = DaoCollections.getDao(Content.class);
        Content content = contentDao.queryForId(flow.getRequest().getContentId());
        Multipart multipart = new Multipart();
        multipart.parse(flow.getRequest(), content.getRawContent(), StandardCharsets.UTF_8.toString(), new PartHandler() {
            @Override
            public void handleFormItem(String name, String value) {
                System.out.println("name: " + name + ", value: " + value);
            }

            @Override
            public void handleFileItem(String name, FileItem fileItem) {
                System.out.println("name: " + name + ", fileName: " + fileItem.getFileName() + ", contentType: " + fileItem.getContentType() + ", contentLength: " + fileItem.getContentLength());
            }
        });
    }
}