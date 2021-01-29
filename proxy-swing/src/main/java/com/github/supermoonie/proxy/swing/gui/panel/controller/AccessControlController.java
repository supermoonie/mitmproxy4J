package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.Application;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AccessControl;
import com.github.supermoonie.proxy.swing.gui.panel.AccessControlDialog;
import com.j256.ormlite.dao.Dao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2021/1/18
 */
public class AccessControlController extends AccessControlDialog {

    public AccessControlController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getAddButton().addActionListener(e -> {
            int editingRow = getAccessTable().getEditingRow();
            if (-1 == editingRow) {
                DefaultTableModel model = (DefaultTableModel) getAccessTable().getModel();
                model.addRow(new String[]{""});
            }
        });
        getRemoveButton().addActionListener(e -> {
            JTable accessTable = getAccessTable();
            int selectedRow = accessTable.getSelectedRow();
            int editingRow = accessTable.getEditingRow();
            int row = -1 != editingRow ? editingRow : selectedRow;
            if (-1 != row) {
                DefaultTableModel model = (DefaultTableModel) accessTable.getModel();
                model.removeRow(row);
                accessTable.clearSelection();
                accessTable.updateUI();
                accessTable.setShowHorizontalLines(true);
            }
        });
        getOkButton().addActionListener(e -> {
            Set<String> ipSet = new HashSet<>();
            DefaultTableModel model = (DefaultTableModel) getAccessTable().getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                String ip = Objects.requireNonNullElse(model.getValueAt(row, 0), "").toString();
                if (!"".equals(ip)) {
                    ipSet.add(ip);
                }
            }
            Dao<AccessControl, Integer> accessDao = DaoCollections.getDao(AccessControl.class);
            try {
                accessDao.deleteBuilder().delete();
                List<AccessControl> list = ipSet.stream().map(ip -> {
                    AccessControl ac = new AccessControl();
                    ac.setAccessIp(ip);
                    ac.setTimeCreated(new Date());
                    return ac;
                }).collect(Collectors.toList());
                accessDao.create(list);
            } catch (SQLException ex) {
                Application.showError(ex);
            }
            setVisible(false);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
    }
}
