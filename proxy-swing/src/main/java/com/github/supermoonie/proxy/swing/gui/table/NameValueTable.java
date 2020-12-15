package com.github.supermoonie.proxy.swing.gui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * @author supermoonie
 * @since 2020/12/7
 */
public class NameValueTable extends CurdTable {

    private final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class, Object.class};

    public NameValueTable() {
        super(new DefaultTableModel(null, new String[]{"Name", "Value", ""}));
        super.setShowHorizontalLines(true);
        super.setShowVerticalLines(true);
        super.setRowSelectionAllowed(true);
        super.setColumnSelectionAllowed(true);
        int columnCount = super.getColumnModel().getColumnCount();
        TableColumn lastColumn = super.getColumnModel().getColumn(columnCount - 1);
        lastColumn.setCellRenderer(new ButtonRenderer("Del"));
        lastColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        lastColumn.setPreferredWidth(200);
        DefaultCellEditor nameCellEditor = new DefaultCellEditor(new JTextField());
        nameCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(0).setCellEditor(nameCellEditor);
        DefaultCellEditor valueCellEditor = new DefaultCellEditor(new JTextField());
        valueCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(1).setCellEditor(valueCellEditor);
        super.getColumnModel().getColumn(0).setPreferredWidth(600);
        super.getColumnModel().getColumn(1).setPreferredWidth(600);
        getModel().addTableModelListener(e -> {
            if ((e.getLastRow() + 1) == getModel().getRowCount()) {
                addRow();
            }
        });
        ((DefaultTableModel) getModel()).addRow(new Object[]{"", "", "Del"});
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }
}
