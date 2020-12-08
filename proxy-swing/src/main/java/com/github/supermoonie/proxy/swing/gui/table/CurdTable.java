package com.github.supermoonie.proxy.swing.gui.table;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/12/7
 */
public class CurdTable extends JTable {

    public CurdTable() {

    }

    public static class CurdTableData {
        private final boolean selected;
        private final String name;
        private final String value;

        public CurdTableData(boolean selected, String name, String value) {
            this.selected = selected;
            this.name = name;
            this.value = value;
        }

        public boolean isSelected() {
            return selected;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    private class CurdTableModel extends AbstractTableModel {
        final String[] columnNames = new String[]{"", "Name", "Value", "Add/Del"};
        final Class<?>[] columnTypes = new Class<?>[]{Boolean.class, String.class, String.class, Object.class};
        private final List<CurdTableData> data;

        private CurdTableModel() {
            this.data = new LinkedList<>();
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            CurdTableData tableData = this.data.get(row);
            Object value = null;
            switch (column) {
                case 0:
                    value = tableData.isSelected();
                    break;
                case 1:
                    value = tableData.getName();
                    break;
                case 2:
                    value = tableData.getValue();
                case 3:
                    break;
            }
            return value;
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == 3) {
                fireTableCellUpdated(row, column);
                remove((CurdTableData) value);
            }
        }

        public void add(CurdTableData row) {
            int startIndex = getRowCount();
            this.data.add(row);
            fireTableRowsInserted(startIndex, getRowCount() - 1);
        }

        public void remove(CurdTableData row) {
            int startIndex = data.indexOf(row);
            data.remove(row);
            fireTableRowsInserted(startIndex, startIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
    }

//    private class AddDelPane extends JPanel {
//        private JButton accept;
//        private JButton reject;
//        private String state;
//    }
}
