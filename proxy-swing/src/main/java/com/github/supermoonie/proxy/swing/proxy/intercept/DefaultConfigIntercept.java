package com.github.supermoonie.proxy.swing.proxy.intercept;

import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AllowBlock;
import com.github.supermoonie.proxy.swing.entity.RequestMap;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * @author supermoonie
 * @since 2020/9/26
 */
public class DefaultConfigIntercept extends ConfigurableIntercept {

    public static final DefaultConfigIntercept INSTANCE = new DefaultConfigIntercept();

    private DefaultConfigIntercept() {
        super.setAllowFlag(ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_ALLOW_LIST_ENABLE, ApplicationPreferences.VALUE_ALLOW_LIST_ENABLE));
        super.setBlockFlag(ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_BLOCK_LIST_ENABLE, ApplicationPreferences.VALUE_BLOCK_LIST_ENABLE));
        super.setRemoteMapFlag(ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_REMOTE_MAP_ENABLE, ApplicationPreferences.VALUE_REMOTE_MAP_ENABLE));
        Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
        Dao<RequestMap, Integer> requestMapDao = DaoCollections.getDao(RequestMap.class);
        try {
            List<AllowBlock> allowBlockList = allowBlockDao.queryForAll();
            for (AllowBlock allowBlock : allowBlockList) {
                if (allowBlock.getEnable().equals(AllowBlock.ENABLE)) {
                    if (allowBlock.getType().equals(AllowBlock.TYPE_ALLOW)) {
                        super.getAllowUriList().add(allowBlock.getLocation());
                    } else {
                        super.getBlockUriList().add(allowBlock.getLocation());
                    }
                }
            }
            List<RequestMap> requestMapList = requestMapDao.queryBuilder().where()
                    .eq(RequestMap.MAP_TYPE_FIELD_NAME, RequestMap.TYPE_REMOTE)
                    .and().eq(RequestMap.ENABLE_FIELD_NAME, RequestMap.ENABLE).query();
            for (RequestMap reqMap : requestMapList) {
                if (reqMap.getEnable().equals(RequestMap.ENABLE)) {
                    super.getRemoteUriMap().put(reqMap.getFromUrl(), reqMap.getToUrl());
                }
            }
        } catch (SQLException e) {
            Application.showError(e);
        }
    }
}
