package com.github.supermoonie.proxy.fx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.entity.ConnectionOverview;
import com.github.supermoonie.proxy.fx.mapper.ConnectionOverviewMapper;
import com.github.supermoonie.proxy.fx.service.ConnectionOverviewService;
import com.github.supermoonie.proxy.fx.util.JSON;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class ConnectionOverviewServiceImpl implements ConnectionOverviewService {

    @Resource
    private ConnectionOverviewMapper connectionOverviewMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public String saveClientInfo(ConnectionInfo connectionInfo, String requestId) {
        ConnectionOverview overview = new ConnectionOverview();
        overview.setId(UUID.randomUUID().toString());
        overview.setRequestId(requestId);
        overview.setClientHost(connectionInfo.getClientHost());
        overview.setClientPort(connectionInfo.getClientPort());
        overview.setClientSessionId(connectionInfo.getClientSessionId());
        overview.setClientProtocol(connectionInfo.getClientProtocol());
        overview.setClientCipherSuite(connectionInfo.getClientCipherSuite());
        connectionOverviewMapper.insert(overview);
        return overview.getId();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int updateServerInfo(ConnectionInfo connectionInfo, String requestId) {
        QueryWrapper<ConnectionOverview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("request_id", requestId);
        ConnectionOverview connectionOverview = connectionOverviewMapper.selectOne(queryWrapper);
        connectionOverview.setConnectStartTime(connectionInfo.getConnectStartTime());
        connectionOverview.setConnectEndTime(connectionInfo.getConnectEndTime());
//        connectionOverview.setDnsServer(connectionInfo.getDnsServer());
        connectionOverview.setDnsStartTime(connectionInfo.getDnsStartTime());
        connectionOverview.setDnsEndTime(connectionInfo.getDnsEndTime());
        connectionOverview.setRemoteIp(JSON.toJsonString(connectionInfo.getRemoteAddressList().stream().map(InetAddress::getHostAddress).collect(Collectors.toList())));
        connectionOverview.setServerSessionId(connectionInfo.getServerSessionId());
        connectionOverview.setServerProtocol(connectionInfo.getServerProtocol());
        connectionOverview.setServerCipherSuite(connectionInfo.getServerCipherSuite());
        if (connectionInfo.isUseSecondProxy()) {
            connectionOverview.setUseSecondProxy(1);
//            connectionOverview.setSecondProxyHost(connectionInfo.getSecondProxyHost());
//            connectionOverview.setSecondProxyPort(connectionInfo.getSecondProxyPort());
        } else {
            connectionOverview.setUseSecondProxy(0);
        }
        return connectionOverviewMapper.updateById(connectionOverview);
    }
}
