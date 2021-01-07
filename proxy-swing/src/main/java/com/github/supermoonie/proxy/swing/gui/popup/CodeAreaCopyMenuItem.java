package com.github.supermoonie.proxy.swing.gui.popup;

import com.github.supermoonie.proxy.swing.util.ClipboardUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

/**
 * @author supermoonie
 * @since 2021/1/7
 */
public class CodeAreaCopyMenuItem extends JMenuItem {

    private final RSyntaxTextArea codeArea;

    public CodeAreaCopyMenuItem(String text, RSyntaxTextArea codeArea) {
        super(text);
        this.codeArea = codeArea;
        init();
    }

    public CodeAreaCopyMenuItem(String text, Icon icon, RSyntaxTextArea codeArea) {
        super(text, icon);
        this.codeArea = codeArea;
        init();
    }

    private void init() {
        super.addActionListener(e -> {
            String selectedText = codeArea.getSelectedText();
            if (null != selectedText) {
                ClipboardUtil.copyText(selectedText);
            }
        });
    }

}
