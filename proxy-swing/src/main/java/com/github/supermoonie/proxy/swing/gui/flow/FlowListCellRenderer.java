package com.github.supermoonie.proxy.swing.gui.flow;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/11/28
 */
public class FlowListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Flow flow = (Flow) value;
        label.setToolTipText(flow.getUrl());
        label.setText(flow.getUrl());
        label.setIcon(flow.getIcon());
        return label;
    }
}
