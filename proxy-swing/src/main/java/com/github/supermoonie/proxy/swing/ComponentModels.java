package com.github.supermoonie.proxy.swing;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.tree.TreeModel;
import java.awt.*;

/**
 * @author supermoonie
 * @date 2020-11-21
 */
public class ComponentModels {

    public static TreeModel getTreeModel(Component root) {
        return getTreeTableModel(root);
    }

    //TODO implement column names?
    public static TreeTableModel getTreeTableModel(Component root) {
        return new AbstractTreeTableModel(root) {

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(Object node, int column) {
                Component c = (Component) node;
                Object o = null;

                switch (column) {
                    case 0:
                        o = c;
                        break;
                    case 1:
                        o = c.getName();
                        break;
                    case 2:
                        if (c.isShowing()) {
                            o = c.getLocationOnScreen();
                        }
                        break;
                    case 3:
                        o = c.getSize();
                        break;
                    default:
                        //does nothing
                        break;
                }

                return o;
            }


            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Component.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Point.class;
                    case 3:
                        return Dimension.class;
                }
                return super.getColumnClass(column);
            }

            @Override
            public Object getChild(Object parent, int index) {
                return ((Container) parent).getComponent(index);
            }

            @Override
            public int getChildCount(Object parent) {
                return parent instanceof Container ? ((Container) parent).getComponentCount() : 0;
            }

            @Override
            public int getIndexOfChild(Object parent, Object child) {
                Component[] children = ((Container) parent).getComponents();

                for (int i = 0, len = children.length; i < len; i++) {
                    if (child == children[i]) {
                        return i;
                    }
                }

                return -1;
            }

        };
    }
}
