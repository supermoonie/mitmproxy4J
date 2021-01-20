package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.RequestMap;
import com.github.supermoonie.proxy.swing.gui.panel.LocalMapDialog;
import com.github.supermoonie.proxy.swing.proxy.intercept.DefaultLocalMapIntercept;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2021/1/20
 */
public class LocalMapDialogController extends LocalMapDialog {

    public LocalMapDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addActionListener(e -> {
            getRequestMapTable().setEnabled(getEnableCheckBox().isSelected());
            getAddButton().setEnabled(getEnableCheckBox().isSelected());
            getRemoveButton().setEnabled(getEnableCheckBox().isSelected());
        });
        getAddButton().addActionListener(e -> {
            getRequestMapTable().clearSelection();
            getLocalMapTableModel().addRow(new Object[]{true, "", ""});
            getRequestMapTable().setShowHorizontalLines(true);
            getRequestMapTable().setShowVerticalLines(true);
        });
        getRemoveButton().addActionListener(e -> {
            int[] selectedRows = getRequestMapTable().getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                getLocalMapTableModel().removeRow(row);
            }
            getRequestMapTable().setShowHorizontalLines(true);
            getRequestMapTable().setShowVerticalLines(true);
        });
        getOkButton().addActionListener(e -> {
            DefaultLocalMapIntercept.INSTANCE.setLocalMapFlag(getEnableCheckBox().isSelected());
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_LOCAL_MAP_ENABLE, getEnableCheckBox().isSelected());
            Dao<RequestMap, Integer> requestMapDao = DaoCollections.getDao(RequestMap.class);
            try {
                DefaultLocalMapIntercept.INSTANCE.getLocalMap().clear();
                DeleteBuilder<RequestMap, Integer> deleteBuilder = requestMapDao.deleteBuilder();
                deleteBuilder.where().eq(RequestMap.MAP_TYPE_FIELD_NAME, RequestMap.TYPE_LOCAL);
                deleteBuilder.delete();
                int rowCount = getRequestMapTable().getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    boolean enable = (boolean) getRequestMapTable().getValueAt(i, 0);
                    String from = (String) getRequestMapTable().getValueAt(i, 1);
                    String to = (String)getRequestMapTable(). getValueAt(i, 2);
                    if (null != from && !"".equals(from) && null != to && !"".equals(to)) {
                        RequestMap requestMap = new RequestMap();
                        requestMap.setFromUrl(from);
                        requestMap.setToUrl(to);
                        requestMap.setMapType(RequestMap.TYPE_LOCAL);
                        requestMap.setEnable(enable ? RequestMap.ENABLE : RequestMap.DISABLE);
                        requestMap.setTimeCreated(new Date());
                        requestMapDao.create(requestMap);
                        if (enable) {
                            DefaultLocalMapIntercept.INSTANCE.getLocalMap().put(from, to);
                        }
                    }
                }
            } catch (SQLException t) {
                Application.showError(t);
            }
            setVisible(false);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
    }
}
