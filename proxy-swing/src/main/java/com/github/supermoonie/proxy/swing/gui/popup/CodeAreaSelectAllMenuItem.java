package com.github.supermoonie.proxy.swing.gui.popup;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

/**
 * @author supermoonie
 * @since 2021/1/7
 */
public class CodeAreaSelectAllMenuItem extends JMenuItem {

    private final RSyntaxTextArea codeArea;

    public CodeAreaSelectAllMenuItem(String text, RSyntaxTextArea codeArea) {
        super(text);
        this.codeArea = codeArea;
        init();
    }

    public CodeAreaSelectAllMenuItem(String text, Icon icon, RSyntaxTextArea codeArea) {
        super(text, icon);
        this.codeArea = codeArea;
        init();
    }

    private void init() {
        super.addActionListener(e -> codeArea.selectAll());
    }
}
