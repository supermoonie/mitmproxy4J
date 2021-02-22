package com.github.supermoonie.proxy.fx.dao;

import com.github.supermoonie.proxy.fx.entity.Content;
import com.j256.ormlite.dao.Dao;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/11/25
 */
public final class ContentDao {

    private ContentDao() {
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
