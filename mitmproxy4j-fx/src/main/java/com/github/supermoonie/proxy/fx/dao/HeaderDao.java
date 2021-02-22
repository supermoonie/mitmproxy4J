package com.github.supermoonie.proxy.fx.dao;

import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.j256.ormlite.dao.Dao;
import io.netty.handler.codec.http.HttpHeaders;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author supermoonie
 * @since 2020/11/25
 */
public final class HeaderDao {

    private HeaderDao() {
        throw new UnsupportedOperationException();
    }

    public static void saveHeaders(HttpHeaders headers, Integer requestId, Integer responseId) throws SQLException {
        Set<String> names = headers.names();
        Dao<Header, Integer> dao = DaoCollections.getDao(Header.class);
        for (String name : names) {
            List<String> valueList = headers.getAll(name);
            for (String value : valueList) {
                Header header = new Header();
                header.setName(name);
                header.setValue(value);
                header.setRequestId(requestId);
                header.setResponseId(responseId);
                header.setTimeCreated(new Date());
                dao.create(header);
            }
        }
    }
}
