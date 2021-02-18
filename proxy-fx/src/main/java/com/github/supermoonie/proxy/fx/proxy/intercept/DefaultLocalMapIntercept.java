package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.entity.RequestMap;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.fx.util.ApplicationPreferences;
import com.github.supermoonie.proxy.intercept.LocalMapIntercept;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/12/28
 */
public class DefaultLocalMapIntercept extends LocalMapIntercept {

    public static final DefaultLocalMapIntercept INSTANCE = new DefaultLocalMapIntercept();

    private DefaultLocalMapIntercept() {
        super.setLocalMapFlag(ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_LOCAL_MAP_ENABLE, ApplicationPreferences.DEFAULT_LOCAL_MAP_ENABLE));
        Dao<RequestMap, Integer> requestMapDao = DaoCollections.getDao(RequestMap.class);
        try {
            List<RequestMap> requestMapList = requestMapDao.queryBuilder().where()
                    .eq(RequestMap.MAP_TYPE_FIELD_NAME, RequestMap.TYPE_LOCAL)
                    .and().eq(RequestMap.ENABLE_FIELD_NAME, RequestMap.ENABLE).query();
            for (RequestMap reqMap : requestMapList) {
                super.getLocalMap().put(reqMap.getFromUrl(), reqMap.getToUrl());
            }
        } catch (SQLException e) {
            AlertUtil.error(e);
        }
    }
}
