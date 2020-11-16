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
     * save connection client info
     * @param connectionInfo {@link ConnectionInfo}
     * @param requestId {@link Request#getId()}
     * @return  {@link ConnectionOverview#getId()}
     */
    String saveClientInfo(ConnectionInfo connectionInfo, String requestId);

    /**
     * update connection server info
     * @param connectionInfo    {@link ConnectionInfo}
     * @param requestId {@link Request#getId()}
     * @return effect rows
     */
    int updateServerInfo(ConnectionInfo connectionInfo, String requestId);
}
