package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.github.supermoonie.proxy.fx.AppPreferences;
import com.github.supermoonie.proxy.fx.dao.DaoCollections;
import com.github.supermoonie.proxy.fx.entity.AllowBlock;
import com.github.supermoonie.proxy.fx.util.AlertUtil;
import com.github.supermoonie.proxy.intercept.ConfigurableIntercept;
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
        super.setAllowFlag(AppPreferences.getState().getBoolean(AppPreferences.KEY_ALLOW_LIST_ENABLE, AppPreferences.DEFAULT_ALLOW_LIST_ENABLE));
        super.setBlockFlag(AppPreferences.getState().getBoolean(AppPreferences.KEY_BLOCK_LIST_ENABLE, AppPreferences.DEFAULT_BLOCK_LIST_ENABLE));
        Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
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
        } catch (SQLException e) {
            AlertUtil.error(e);
        }
    }
}
