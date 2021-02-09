package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.MitmProxy4J;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AllowBlock;
import com.github.supermoonie.proxy.swing.gui.panel.AllowListDialog;
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
 * @since 2021/1/18
 */
public class AllowListDialogController extends AllowListDialog {

    public AllowListDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addActionListener(e -> {
            getAllowTable().setEnabled(getEnableCheckBox().isSelected());
            getAddButton().setEnabled(getEnableCheckBox().isSelected());
            getRemoveButton().setEnabled(getEnableCheckBox().isSelected());
        });
        getAddButton().addActionListener(e -> {
            getAllowTable().clearSelection();
            getAllowTableModel().addRow(new Object[]{true, ""});
            getAllowTable().setShowHorizontalLines(true);
            getAllowTable().setShowVerticalLines(true);
        });
        getRemoveButton().addActionListener(e -> {
            JTable allowTable = getAllowTable();
            int[] selectedRows = allowTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                getAllowTableModel().removeRow(row);
            }
            allowTable.clearSelection();
            allowTable.setShowHorizontalLines(true);
            allowTable.setShowVerticalLines(true);
        });
        getOkButton().addActionListener(e -> {
            DefaultConfigIntercept.INSTANCE.setAllowFlag(getEnableCheckBox().isSelected());
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_ALLOW_LIST_ENABLE, getEnableCheckBox().isSelected());
            Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
            try {
                DeleteBuilder<AllowBlock, Integer> deleteBuilder = allowBlockDao.deleteBuilder();
                deleteBuilder.where().eq(AllowBlock.TYPE_FIELD_NAME, AllowBlock.TYPE_ALLOW);
                deleteBuilder.delete();
                Set<String> allowSet = new HashSet<>();
                int rowCount = getAllowTable().getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    boolean enable = (boolean) getAllowTable().getValueAt(i, 0);
                    String location = (String) getAllowTable().getValueAt(i, 1);
                    if (null != location && !"".equals(location)) {
                        AllowBlock allowBlock = new AllowBlock();
                        allowBlock.setEnable(enable ? AllowBlock.ENABLE : AllowBlock.DISABLE);
                        allowBlock.setType(AllowBlock.TYPE_ALLOW);
                        allowBlock.setLocation(location);
                        allowBlock.setTimeCreated(new Date());
                        allowBlockDao.create(allowBlock);
                    }
                    if (enable) {
                        allowSet.add(location);
                    }
                }
                DefaultConfigIntercept.INSTANCE.getAllowUriList().clear();
                DefaultConfigIntercept.INSTANCE.getAllowUriList().addAll(allowSet);
            } catch (SQLException t) {
                MitmProxy4J.showError(t);
            }
            setVisible(false);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
    }
}
