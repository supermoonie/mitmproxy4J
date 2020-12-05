package com.github.supermoonie.proxy.swing;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import com.github.supermoonie.proxy.swing.gui.ProxyFrameHelper;
import com.github.supermoonie.proxy.swing.gui.lintener.FilterKeyListener;
import org.apache.commons.io.FileUtils;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

/**
 * @author supermoonie
 * @since 2020/11/26
 */
public class TextEditorDemo extends JFrame {

    public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setFont(table.getFont());
            if (hasFocus) {
                setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
                if (table.isCellEditable(row, column)) {
                    setForeground( UIManager.getColor("Table.focusCellForeground") );
                    setBackground( UIManager.getColor("Table.focusCellBackground") );
                }
            } else {
                setBorder(new EmptyBorder(1, 2, 1, 2));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private static final long serialVersionUID = 1L;

    public TextEditorDemo() throws IOException {

        JPanel cp = new JPanel(new BorderLayout());

//        RSyntaxTextArea textArea = new RSyntaxTextArea();
//        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
//        textArea.setCodeFoldingEnabled(true);
        String js = FileUtils.readFileToString(new File("/Users/supermoonie/Desktop/index.js"), StandardCharsets.UTF_8);
//        String js = "function() {\n" +
//                "   var foo = 'bar';\n" +
//                "   console.log(foo);\n" +
//                "}";
//        textArea.setText(js);
//        textArea.setEditable(false);
//        RTextScrollPane sp = new RTextScrollPane(textArea);
        String[] lines = js.split("\n");
        JList<String> list = new JList<>(lines);
//        JLabel label = new JLabel(js);
        JScrollPane sp = new JScrollPane(list);
//        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
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
