package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AllowBlock;
import com.github.supermoonie.proxy.swing.gui.panel.BlockListDialog;
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
 * @since 2021/1/20
 */
public class BlockListDialogController extends BlockListDialog {

    public BlockListDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addActionListener(e -> {
            getBlockTable().setEnabled(getEnableCheckBox().isSelected());
            getAddButton().setEnabled(getEnableCheckBox().isSelected());
            getRemoveButton().setEnabled(getEnableCheckBox().isSelected());
        });
        getAddButton().addActionListener(e -> {
            getBlockTable().clearSelection();
            getBlockTableModel().addRow(new Object[]{true, ""});
            getBlockTable().setShowHorizontalLines(true);
            getBlockTable().setShowVerticalLines(true);
        });
        getRemoveButton().addActionListener(e -> {
            JTable allowTable = getBlockTable();
            int[] selectedRows = allowTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                getBlockTableModel().removeRow(row);
            }
            allowTable.clearSelection();
            allowTable.setShowHorizontalLines(true);
            allowTable.setShowVerticalLines(true);
        });
        getOkButton().addActionListener(e -> {
            DefaultConfigIntercept.INSTANCE.setBlockFlag(getEnableCheckBox().isSelected());
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_BLOCK_LIST_ENABLE, getEnableCheckBox().isSelected());
            Dao<AllowBlock, Integer> allowBlockDao = DaoCollections.getDao(AllowBlock.class);
            try {
                DeleteBuilder<AllowBlock, Integer> deleteBuilder = allowBlockDao.deleteBuilder();
                deleteBuilder.where().eq(AllowBlock.TYPE_FIELD_NAME, AllowBlock.TYPE_BLOCK);
                deleteBuilder.delete();
                Set<String> blockSet = new HashSet<>();
                int rowCount = getBlockTable().getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    boolean enable = (boolean) getBlockTable().getValueAt(i, 0);
                    String location = (String) getBlockTable().getValueAt(i, 1);
                    if (null != location && !"".equals(location)) {
                        AllowBlock allowBlock = new AllowBlock();
                        allowBlock.setEnable(enable ? AllowBlock.ENABLE : AllowBlock.DISABLE);
                        allowBlock.setType(AllowBlock.TYPE_BLOCK);
                        allowBlock.setLocation(location);
                        allowBlock.setTimeCreated(new Date());
                        allowBlockDao.create(allowBlock);
                    }
                    if (enable) {
                        blockSet.add(location);
                    }
                }
                DefaultConfigIntercept.INSTANCE.getBlockUriList().clear();
                DefaultConfigIntercept.INSTANCE.getBlockUriList().addAll(blockSet);
            } catch (SQLException t) {
                Application.showError(t);
            }
            setVisible(false);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
    }
}
