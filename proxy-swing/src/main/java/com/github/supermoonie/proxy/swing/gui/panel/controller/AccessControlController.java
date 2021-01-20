package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.gui.panel.AccessControlDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
            ApplicationPreferences.setAccessControl(ipSet);
            setVisible(false);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
    }
}
