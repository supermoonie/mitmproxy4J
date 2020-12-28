package com.github.supermoonie.proxy.swing.gui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/12/28
 */
public class MapLocalDialog extends JDialog {

    public MapLocalDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        super.setLayout(new BorderLayout());
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel centerPanel = new JPanel();
        BoxLayout centerLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        centerPanel.setLayout(centerLayout);
        JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT){{
            setHgap(0);
        }});
        fromPanel.add(new JLabel("From:"));
        centerPanel.add(fromPanel);
        centerPanel.add(new JTextField());
        JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.LEFT){{
            setHgap(0);
        }});
        toPanel.add(new JLabel("To:"));
        centerPanel.add(toPanel);
        centerPanel.add(new JTextField());
        container.add(centerPanel, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT){{
            setHgap(0);
        }}){{
            getInsets(new Insets(0, 0, 0, 0));
        }};
        southPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
        southPanel.add(new JButton("Cancel"));
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(new JButton("Ok"));
        container.add(southPanel, BorderLayout.SOUTH);
        add(container, BorderLayout.CENTER);
        setSize(262, 173);
        setResizable(false);
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
