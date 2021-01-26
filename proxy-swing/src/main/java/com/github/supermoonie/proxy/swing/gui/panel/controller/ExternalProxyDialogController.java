package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.ProxyType;
import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AllowBlock;
import com.github.supermoonie.proxy.swing.entity.ExternalProxy;
import com.github.supermoonie.proxy.swing.gui.panel.ExternalProxyDialog;
import com.github.supermoonie.proxy.swing.proxy.intercept.DefaultConfigIntercept;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author supermoonie
 * @since 2021/1/26
 */
public class ExternalProxyDialogController extends ExternalProxyDialog {


    public ExternalProxyDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addActionListener(e -> {
            getProxyTable().setEnabled(getEnableCheckBox().isSelected());
            getAddButton().setEnabled(getEnableCheckBox().isSelected());
            getRemoveButton().setEnabled(getEnableCheckBox().isSelected());
        });
        getAddButton().addActionListener(e -> {
            getProxyTable().clearSelection();
            getProxyTableModel().addRow(new Object[]{true, "*", "", "", false, "", "", "HTTP"});
            getProxyTable().setShowHorizontalLines(true);
            getProxyTable().setShowVerticalLines(true);
        });
        getRemoveButton().addActionListener(e -> {
            JTable proxyTable = getProxyTable();
            int[] selectedRows = proxyTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                getProxyTableModel().removeRow(row);
            }
            proxyTable.clearSelection();
            proxyTable.setShowHorizontalLines(true);
            proxyTable.setShowVerticalLines(true);
        });
        getOkButton().addActionListener(e -> {
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_EXTERNAL_PROXY_ENABLE, getEnableCheckBox().isSelected());
            Dao<ExternalProxy, Integer> dao = DaoCollections.getDao(ExternalProxy.class);
            try {
                dao.deleteBuilder().delete();
                int rowCount = getProxyTable().getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    boolean enable = (boolean) getProxyTable().getValueAt(i, 0);
                    String host = (String) getProxyTable().getValueAt(i, 1);
                    String proxyHost = (String) getProxyTable().getValueAt(i, 2);
                    int proxyPort = Integer.parseInt(getProxyTable().getValueAt(i, 3).toString());
                    boolean authEnable = (boolean) getProxyTable().getValueAt(i, 4);
                    String user = (String) getProxyTable().getValueAt(i, 5);
                    String pwd = (String) getProxyTable().getValueAt(i, 6);
                    String proxyType = (String) getProxyTable().getValueAt(i, 7);
                    if (null != host && !"".equals(host) && null != proxyHost && !"".equals(proxyHost) && proxyPort > 0) {
                        ExternalProxy proxy = new ExternalProxy();
                        proxy.setEnable(enable ? ExternalProxy.ENABLE : ExternalProxy.DISABLE);
                        proxy.setHost(host);
                        proxy.setProxyHost(proxyHost);
                        proxy.setProxyPort(proxyPort);
                        proxy.setProxyAuth(authEnable ? ExternalProxy.ENABLE : ExternalProxy.DISABLE);
                        proxy.setProxyUser(user);
                        proxy.setProxyPwd(pwd);
                        proxy.setProxyType(ProxyType.valueOf(proxyType).getCode());
                        proxy.setTimeCreated(new Date());
                        dao.create(proxy);
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
