package com.github.supermoonie.proxy.swing.gui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/12/6
 */
public class FlowPanel extends JPanel {

    private final JTextField filterTextField;

    private final JTabbedPane tabContainer;

    private final JPanel structureTab;

    public FlowPanel() {
        super.setLayout(new BorderLayout());
        super.setMinimumSize(new Dimension(200, 0));
        filterTextField = new JTextField();
        tabContainer = new JTabbedPane();
        structureTab = new JPanel(new BorderLayout());
        structureTab.setMinimumSize(new Dimension(100, 0));

    }

    public JTextField getFilterTextField() {
        return filterTextField;
    }
}
