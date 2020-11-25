package com.github.supermoonie.proxy.swing.service;

import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.Content;
import com.j256.ormlite.dao.Dao;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/11/25
 */
public final class ContentService {

    private ContentService() {
        throw new UnsupportedOperationException();
    }

    public static int saveContent(ByteBuf buf) throws SQLException {
        buf.markReaderIndex();
        Content content = new Content();
        content.setTimeCreated(new Date());
        content.setRawContent(ByteBufUtil.getBytes(buf));
        buf.resetReaderIndex();
        Dao<Content, Integer> dao = DaoCollections.getDao(Content.class);
        dao.create(content);
        return content.getId();
    }
}
