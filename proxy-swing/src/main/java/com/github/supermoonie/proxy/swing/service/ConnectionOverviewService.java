package com.github.supermoonie.proxy.swing.service;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.ConnectionOverview;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public final class ConnectionOverviewService {

    private ConnectionOverviewService() {
        throw new UnsupportedOperationException();
    }

    public static int saveClientInfo(ConnectionInfo connectionInfo, int requestId) throws SQLException {
        ConnectionOverview overview = new ConnectionOverview();
        overview.setRequestId(requestId);
        overview.setClientHost(connectionInfo.getClientHost());
        overview.setClientPort(connectionInfo.getClientPort());
        overview.setClientSessionId(connectionInfo.getClientSessionId());
        overview.setClientProtocol(connectionInfo.getClientProtocol());
        overview.setClientCipherSuite(connectionInfo.getClientCipherSuite());
        overview.setTimeCreated(new Date());
        Dao<ConnectionOverview, Integer> dao = DaoCollections.getDao(ConnectionOverview.class);
        dao.create(overview);
        return overview.getId();
    }
}
