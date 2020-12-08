package com.github.supermoonie.proxy.swing.gui.table;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * 不可编辑的TableModel
 *
 * @author supermoonie
 * @since 2020/11/29
 */
public class NoneEditTableModel extends DefaultTableModel {

    public NoneEditTableModel() {
    }

    public NoneEditTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public NoneEditTableModel(Vector<?> columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public NoneEditTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public NoneEditTableModel(Vector<? extends Vector> data, Vector<?> columnNames) {
        super(data, columnNames);
    }

    public NoneEditTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


}
