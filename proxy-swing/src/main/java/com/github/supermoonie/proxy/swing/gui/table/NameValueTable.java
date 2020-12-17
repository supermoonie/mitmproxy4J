package com.github.supermoonie.proxy.swing.gui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

/**
 * @author supermoonie
 * @since 2020/12/7
 */
public class NameValueTable extends CurdTable {

    private final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class, Object.class};
    protected ButtonEditor buttonEditor = new ButtonEditor(new JCheckBox());

    public NameValueTable() {
        super(new DefaultTableModel(null, new String[]{"Name", "Value", ""}));
        super.setShowHorizontalLines(true);
        super.setShowVerticalLines(true);
        super.setRowSelectionAllowed(true);
        super.setColumnSelectionAllowed(true);
        int columnCount = super.getColumnModel().getColumnCount();
        TableColumn lastColumn = super.getColumnModel().getColumn(columnCount - 1);
        lastColumn.setCellRenderer(new ButtonRenderer("Del"));
        lastColumn.setCellEditor(buttonEditor);
        lastColumn.setPreferredWidth(200);
        JTextField nameTextField = new JTextField();
        DefaultCellEditor nameCellEditor = new DefaultCellEditor(nameTextField);
        nameTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                nameCellEditor.stopCellEditing();
            }
        });
        nameCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(0).setCellEditor(nameCellEditor);
        JTextField valueTextField = new JTextField();
        DefaultCellEditor valueCellEditor = new DefaultCellEditor(valueTextField);
        valueTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                valueCellEditor.stopCellEditing();
            }
        });
        valueCellEditor.setClickCountToStart(1);
        super.getColumnModel().getColumn(1).setCellEditor(valueCellEditor);
        super.getColumnModel().getColumn(0).setPreferredWidth(600);
        super.getColumnModel().getColumn(1).setPreferredWidth(600);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    public ButtonEditor getButtonEditor() {
        return buttonEditor;
    }
}
