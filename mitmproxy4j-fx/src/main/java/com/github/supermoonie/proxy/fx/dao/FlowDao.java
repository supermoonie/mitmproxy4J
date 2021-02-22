package com.github.supermoonie.proxy.fx.dao;

import com.github.supermoonie.proxy.fx.controller.Flow;
import com.github.supermoonie.proxy.fx.entity.Header;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2021/2/22
 */
public class FlowDao {

    private FlowDao() {
        throw new UnsupportedOperationException();
    }

    public static Flow getFlow(Integer requestId) throws SQLException {
        Dao<Request, Integer> requestDao = DaoCollections.getDao(Request.class);
        Dao<Header, Integer> headerDao = DaoCollections.getDao(Header.class);
        Dao<Response, Integer> responseDao = DaoCollections.getDao(Response.class);
        Flow flow = new Flow();
        Request request = requestDao.queryForId(requestId);
        List<Header> requestHeaders = headerDao.queryBuilder().where().eq(Header.REQUEST_ID_FIELD_NAME, request.getId()).query();
        flow.setRequest(request);
        flow.setRequestHeaders(requestHeaders);
        Response response = responseDao.queryBuilder().where().eq(Response.REQUEST_ID_FIELD_NAME, request.getId()).queryForFirst();
        if (null != response) {
            List<Header> responseHeaders = headerDao.queryBuilder().where().eq(Header.RESPONSE_ID_FIELD_NAME, response.getId()).query();
            flow.setResponse(response);
            flow.setResponseHeaders(responseHeaders);
        }
        return flow;
    }
}
