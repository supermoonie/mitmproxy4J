package com.github.supermoonie.proxy.swing.service;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.ConnectionOverview;
import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.dao.Dao;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void updateServerInfo(ConnectionInfo connectionInfo, int requestId) throws SQLException {
        Dao<ConnectionOverview, Integer> dao = DaoCollections.getDao(ConnectionOverview.class);
        List<ConnectionOverview> connectionOverviews = dao.queryForEq(ConnectionOverview.REQUEST_ID_FIELD_NAME, requestId);
        if (null != connectionOverviews && connectionOverviews.size() > 0) {
            ConnectionOverview connectionOverview = connectionOverviews.get(0);
            connectionOverview.setConnectStartTime(0 == connectionInfo.getConnectStartTime() ? null : connectionInfo.getConnectStartTime());
            connectionOverview.setConnectEndTime(0 == connectionInfo.getConnectEndTime() ? null : connectionInfo.getConnectEndTime());
            connectionOverview.setDnsServer(connectionInfo.getDnsServer());
            connectionOverview.setDnsStartTime(0 == connectionInfo.getDnsStartTime() ? null : connectionInfo.getDnsStartTime());
            connectionOverview.setDnsEndTime(0 == connectionInfo.getDnsEndTime() ? null : connectionInfo.getDnsEndTime());
            if (null != connectionInfo.getRemoteAddressList()) {
                connectionOverview.setRemoteIp(Jackson.toJsonString(connectionInfo.getRemoteAddressList().stream().map(InetAddress::getHostAddress).collect(Collectors.toList())));
            }
            connectionOverview.setServerSessionId(connectionInfo.getServerSessionId());
            connectionOverview.setServerProtocol(connectionInfo.getServerProtocol());
            connectionOverview.setServerCipherSuite(connectionInfo.getServerCipherSuite());
            if (connectionInfo.isUseSecondProxy()) {
                connectionOverview.setUseSecondProxy(1);
//                connectionOverview.setSecondProxyHost(connectionInfo.getSecondProxyHost());
//                connectionOverview.setSecondProxyPort(connectionInfo.getSecondProxyPort());
            } else {
                connectionOverview.setUseSecondProxy(0);
            }
            dao.update(connectionOverview);
        }
    }
}
