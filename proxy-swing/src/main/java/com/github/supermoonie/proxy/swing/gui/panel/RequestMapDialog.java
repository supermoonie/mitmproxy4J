package com.github.supermoonie.proxy.swing.gui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/12/28
 */
public class RequestMapDialog extends JDialog {

    private final JTextField fromTextField = new JTextField();
    private final JTextField toTextField = new JTextField();
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton confirmButton = new JButton("OK");

    public RequestMapDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        fromTextField.setPreferredSize(new Dimension(290, 25));
        fromTextField.setMinimumSize(new Dimension(290, 23));
        toTextField.setPreferredSize(new Dimension(290, 25));
        toTextField.setMinimumSize(new Dimension(290, 23));
        cancelButton.addActionListener(e -> setVisible(false));
        super.setLayout(new BorderLayout());
        JPanel container = new JPanel(new BorderLayout(){{
            setVgap(20);setHgap(0);
        }});
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel centerPanel = new JPanel();
        BoxLayout centerLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        centerPanel.setLayout(centerLayout);
        JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT){{
            setHgap(0);setVgap(0);
        }}){{
            setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        }};
        fromPanel.add(new JLabel("From:"));
        centerPanel.add(fromPanel);
        centerPanel.add(fromTextField);
        JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.LEFT){{
            setHgap(0);setVgap(0);
        }}){{
            setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        }};
        toPanel.add(new JLabel("To:"));
        centerPanel.add(toPanel);
        centerPanel.add(toTextField);
        container.add(centerPanel, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT){{
            setHgap(0);setVgap(0);
        }}){{
            getInsets(new Insets(0, 0, 0, 0));
        }};
        southPanel.add(cancelButton);
        southPanel.add(Box.createHorizontalStrut(10));
        southPanel.add(confirmButton);
        getRootPane().setDefaultButton(confirmButton);
        container.add(southPanel, BorderLayout.SOUTH);
        add(container, BorderLayout.CENTER);
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    public JTextField getFromTextField() {
        return fromTextField;
    }

    public JTextField getToTextField() {
        return toTextField;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getConfirmButton() {
        return confirmButton;
    }
}
