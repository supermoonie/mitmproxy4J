package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.github.supermoonie.proxy.fx.AppPreferences;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.entity.RequestMap;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.intercept.RemoteMapIntercept;
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
        super.setRemoteMapFlag(AppPreferences.getState().getBoolean(AppPreferences.KEY_REMOTE_MAP_ENABLE, AppPreferences.DEFAULT_REMOTE_MAP_ENABLE));
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
            AlertUtil.error(e);
        }
    }


}
