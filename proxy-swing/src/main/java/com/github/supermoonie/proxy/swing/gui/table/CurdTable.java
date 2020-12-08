package com.github.supermoonie.proxy.swing.gui.table;

import com.github.supermoonie.proxy.swing.ThemeManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/12/7
 */
public class CurdTable extends JTable {

    public CurdTable(TableModel dm) {
        super(dm);
        super.setShowHorizontalLines(true);
        super.setShowVerticalLines(true);
        super.setRowSelectionAllowed(true);
        super.setColumnSelectionAllowed(true);
        int columnCount = super.getColumnModel().getColumnCount();
        super.setDefaultRenderer(Object.class, new BorderLessTableCellRenderer());
        TableColumn lastColumn = super.getColumnModel().getColumn(columnCount - 1);
        lastColumn.setCellRenderer(new ButtonRenderer());
        lastColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        super.getColumnModel().getColumn(0).setPreferredWidth(600);
        super.getColumnModel().getColumn(1).setPreferredWidth(600);
        lastColumn.setPreferredWidth(200);
        getModel().addTableModelListener(e -> {
            if ((e.getLastRow() + 1) == getModel().getRowCount()) {
                addRow();
            }
        });
        ((DefaultTableModel) getModel()).addRow(new Object[]{"", "", "Del"});
    }

    private void addRow() {
        int rowCount = getModel().getRowCount();
        int columnCount = getModel().getColumnCount();
        boolean addFlag = false;
        if (rowCount > 0) {
            for (int i = 0; i < columnCount - 1; i++) {
                Object value = getModel().getValueAt(rowCount - 1, i);
                if (!value.toString().equals("")) {
                    addFlag = true;
                    break;
                }
            }
        } else {
            addFlag = true;
        }
        if (addFlag) {
            ((DefaultTableModel) getModel()).addRow(new Object[]{"", "", "Del"});
        }
    }

    static class BorderLessTableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int col) {
            return super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    false,
                    row,
                    col
            );
        }
    }

    static class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
            if (ThemeManager.isDark()) {
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#5e6364")));
            } else {
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#f7f7f7")));
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.decode("#0095ff"));
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            int columnCount = table.getModel().getColumnCount();
            for (int i = 0; i < columnCount - 1; i++) {
                Object v = table.getModel().getValueAt(row, i);
                if (null != v && !v.toString().equals("")) {
                    this.setEnabled(true);
                    return this;
                }
            }
            this.setEnabled(false);
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                int selectedRow = CurdTable.this.getSelectedRow();
                ((DefaultTableModel) CurdTable.this.getModel()).removeRow(selectedRow);
                CurdTable.this.clearSelection();
                addRow();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            return true;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

}
