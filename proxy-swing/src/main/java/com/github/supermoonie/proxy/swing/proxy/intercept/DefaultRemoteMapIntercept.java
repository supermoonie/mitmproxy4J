package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.intercept.RemoteMapIntercept;
import com.github.supermoonie.proxy.swing.MitmProxy4J;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.RequestMap;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/12/28
 */
public class DefaultRemoteMapIntercept extends RemoteMapIntercept {

    public static final DefaultRemoteMapIntercept INSTANCE = new DefaultRemoteMapIntercept();

    private DefaultRemoteMapIntercept() {
        super.setRemoteMapFlag(ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_REMOTE_MAP_ENABLE, ApplicationPreferences.DEFAULT_REMOTE_MAP_ENABLE));
        Dao<RequestMap, Integer> requestMapDao = DaoCollections.getDao(RequestMap.class);
        try {
            List<RequestMap> requestMapList = requestMapDao.queryBuilder().where()
                    .eq(RequestMap.MAP_TYPE_FIELD_NAME, RequestMap.TYPE_REMOTE)
                    .and().eq(RequestMap.ENABLE_FIELD_NAME, RequestMap.ENABLE).query();
            for (RequestMap reqMap : requestMapList) {
                if (reqMap.getEnable().equals(RequestMap.ENABLE)) {
                    super.getRemoteUriMap().put(reqMap.getFromUrl(), reqMap.getToUrl());
                }
            }
        } catch (SQLException e) {
            MitmProxy4J.showError(e);
        }
    }


}
