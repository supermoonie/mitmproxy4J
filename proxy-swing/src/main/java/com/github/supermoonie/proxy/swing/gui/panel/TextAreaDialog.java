package com.github.supermoonie.proxy.swing.gui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2021/1/1
 */
public class TextAreaDialog extends JDialog {

    private final JTextArea textArea;

    public TextAreaDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        textArea = new JTextArea();
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea));
        setSize(500, 160);
        setLocationRelativeTo(owner);
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
