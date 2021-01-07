package com.github.supermoonie.proxy.swing.gui.popup;

import com.github.supermoonie.proxy.swing.util.ClipboardUtil;

import javax.swing.*;

/**
 * @author supermoonie
 * @since 2021/1/7
 */
public class TextAreaPopupMenu extends JPopupMenu {

    private JMenuItem copyMenuItem;
    private JMenuItem selectAllMenuItem;
    private final JTextArea textArea;

    public TextAreaPopupMenu(JTextArea textArea) {
        this.textArea = textArea;
        init();
    }

    public TextAreaPopupMenu(String label, JTextArea textArea) {
        super(label);
        this.textArea = textArea;
        init();
    }

    private void init() {
        copyMenuItem = new JMenuItem("Copy");
        selectAllMenuItem = new JMenuItem("Select All");
        copyMenuItem.addActionListener(e -> {
            String selectedText = textArea.getSelectedText();
            if (null != selectedText) {
                ClipboardUtil.copyText(selectedText);
            }
        });
        selectAllMenuItem.addActionListener(e -> textArea.selectAll());
        super.add(copyMenuItem);
        super.add(selectAllMenuItem);
    }

    public JMenuItem getCopyMenuItem() {
        return copyMenuItem;
    }

    public JMenuItem getSelectAllMenuItem() {
        return selectAllMenuItem;
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
