package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.gui.panel.DnsDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2021/1/29
 */
public class DnsDialogController extends DnsDialog {

    public DnsDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addActionListener(e -> {
            getDnsTable().setEnabled(getEnableCheckBox().isSelected());
            getDnsAddButton().setEnabled(getEnableCheckBox().isSelected());
            getDnsRemoveButton().setEnabled(getEnableCheckBox().isSelected());
        });
        getDnsAddButton().addActionListener(e -> {
            getDnsTable().clearSelection();
            getDnsTableModel().addRow(new Object[]{true, ""});
            getDnsTable().setShowHorizontalLines(true);
            getDnsTable().setShowVerticalLines(true);
        });
        getDnsRemoveButton().addActionListener(e -> {
            JTable allowTable = getDnsTable();
            int[] selectedRows = allowTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                getDnsTableModel().removeRow(row);
            }
            allowTable.clearSelection();
            allowTable.setShowHorizontalLines(true);
            allowTable.setShowVerticalLines(true);
        });
        getOkButton().addActionListener(e -> {
            setVisible(false);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
    }
}
