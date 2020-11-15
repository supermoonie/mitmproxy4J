package com.github.supermoonie.proxy.fx.service.impl;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.entity.ConnectionOverview;
import com.github.supermoonie.proxy.fx.mapper.ConnectionOverviewMapper;
import com.github.supermoonie.proxy.fx.service.ConnectionOverviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

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
    public String save(ConnectionInfo connectionInfo, String requestId) {
        ConnectionOverview overview = new ConnectionOverview();
        overview.setId(UUID.randomUUID().toString());
        overview.setClientHost(connectionInfo.getClientHost());
        overview.setClientPort(connectionInfo.getClientPort());
        overview.setClientSessionId(connectionInfo.getClientSessionId());
        overview.setClientProtocol(connectionInfo.getClientProtocol());
        overview.setClientCipherSuite(connectionInfo.getClientCipherSuite());

        return null;
    }
}
