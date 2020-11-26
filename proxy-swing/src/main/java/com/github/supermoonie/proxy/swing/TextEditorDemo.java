package com.github.supermoonie.proxy.swing;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

/**
 * @author supermoonie
 * @since 2020/11/26
 */
public class TextEditorDemo extends JFrame {

    private static final long serialVersionUID = 1L;

    public TextEditorDemo() throws IOException {

        JPanel cp = new JPanel(new BorderLayout());

        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        textArea.setCodeFoldingEnabled(true);
        String js = FileUtils.readFileToString(new File("/Users/supermoonie/Desktop/index.js"), StandardCharsets.UTF_8);
        textArea.setText(js);
        textArea.setEditable(false);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        cp.add(sp);

        setContentPane(cp);
        setTitle("Text Editor Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

    }

    public static void main(String[] args) {
        // Start all Swing applications on the EDT.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TextEditorDemo().setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
