package com.github.supermoonie.proxy.fx.service;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.fx.entity.ConnectionOverview;
import com.github.supermoonie.proxy.fx.entity.Request;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
public interface ConnectionOverviewService {

    /**
     * save connection info
     * @param connectionInfo {@link ConnectionInfo}
     * @param requestId {@link Request#getId()}
     * @return  {@link ConnectionOverview#getId()}
     */
    String save(ConnectionInfo connectionInfo, String requestId);
}
