package com.github.supermoonie.proxy.swing.service;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.swing.db.Db;
import com.github.supermoonie.proxy.swing.entity.ConnectionOverview;
import org.apache.ibatis.session.SqlSession;

import java.util.UUID;

/**
 * @author supermoonie
 * @since 2020/11/23
 */
public final class ConnectionOverviewService {

    private ConnectionOverviewService() {
        throw new UnsupportedOperationException();
    }

    public static void saveClientInfo(ConnectionInfo connectionInfo, String requestId, SqlSession sqlSession) {
        ConnectionOverview overview = new ConnectionOverview();
        overview.setId(UUID.randomUUID().toString());
        overview.setRequestId(requestId);
        overview.setClientHost(connectionInfo.getClientHost());
        overview.setClientPort(connectionInfo.getClientPort());
        overview.setClientSessionId(connectionInfo.getClientSessionId());
        overview.setClientProtocol(connectionInfo.getClientProtocol());
        overview.setClientCipherSuite(connectionInfo.getClientCipherSuite());
        if (null == sqlSession) {
            try (SqlSession session = Db.sqlSessionFactory().openSession()) {

            }
        } else {

        }
    }
}
