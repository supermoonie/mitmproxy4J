package com.github.supermoonie.proxy.swing.gui.overview;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class ListTreeTableNode extends AbstractMutableTreeTableNode {

    private final List<Object> list;

    public ListTreeTableNode(Object... values) {
        this.list = List.of(values);
    }

    public ListTreeTableNode(List<Object> list) {
        this.list = list;
    }

    public ListTreeTableNode addNode(ListTreeTableNode child) {
        super.add(child);
        return this;
    }

    @Override
    public Object getValueAt(int column) {
        return list.get(column);
    }

    @Override
    public int getColumnCount() {
        return list.size();
    }
}
