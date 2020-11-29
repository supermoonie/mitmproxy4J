package com.github.supermoonie.proxy.swing.gui.flow;

import com.github.supermoonie.proxy.swing.icon.SvgIcons;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/11/29
 */
public class FlowTreeCellRender extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value,
                selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Flow flow = (Flow) node.getUserObject();
        if (null != flow && flow.getFlowType().equals(FlowType.TARGET)) {
            setIcon(Objects.requireNonNullElse(flow.getIcon(), SvgIcons.ANY_TYPE));
        }
        return c;
    }
}
