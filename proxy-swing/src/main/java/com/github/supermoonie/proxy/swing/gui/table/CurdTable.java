package com.github.supermoonie.proxy.swing.gui.table;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author supermoonie
 * @since 2020/12/9
 */
public class CurdTable extends JTable {

    public CurdTable() {
    }

    public CurdTable(TableModel dm) {
        super(dm);
    }

    public CurdTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public CurdTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public CurdTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public CurdTable(Vector<? extends Vector> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
    }

    public CurdTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    public void addRow() {
        int rowCount = getModel().getRowCount();
        int columnCount = getModel().getColumnCount();
        boolean addFlag = false;
        if (rowCount > 0) {
            for (int i = 0; i < columnCount - 1; i++) {
                Object value = getModel().getValueAt(rowCount - 1, i);
                if (null == value || !value.toString().equals("")) {
                    addFlag = true;
                    break;
                }
            }
        } else {
            addFlag = true;
        }
        if (addFlag) {
            Object[] rowData = new Object[columnCount];
            for (int i = 0; i < columnCount - 1; i ++) {
                rowData[i] = "";
            }
            rowData[columnCount - 1] = "Del";
            ((DefaultTableModel) getModel()).addRow(rowData);
        }
    }

    public static class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer(String text) {
            setOpaque(true);
            setText(text);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.gridColor")));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(UIManager.getColor("Table.background"));
            }
            setText((value == null) ? "" : value.toString());
            for (int i = 0; i < 2; i++) {
                Object v = table.getModel().getValueAt(row, i);
                if (null != v && !v.toString().equals("")) {
                    this.setEnabled(true);
                    setForeground(Color.decode("#0095ff"));
                    return this;
                }
            }
            setForeground(Color.GRAY);
            return this;
        }
    }

    public class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
//            button.addActionListener(e -> {
//                fireEditingStopped();
//                int selectedRow = CurdTable.this.getSelectedRow();
//                if (-1 == selectedRow) {
//                    return;
//                }
//                ((DefaultTableModel) CurdTable.this.getModel()).removeRow(selectedRow);
//                CurdTable.this.clearSelection();
//                addRow();
//            });
        }

        public void addActionListener(ActionListener listener) {
            button.addActionListener(listener);
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
        public void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
