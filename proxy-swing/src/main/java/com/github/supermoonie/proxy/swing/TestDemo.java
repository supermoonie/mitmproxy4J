package com.github.supermoonie.proxy.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2020/12/9
 */
public class TestDemo extends JFrame {

    public TestDemo() throws HeadlessException {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("center"), BorderLayout.CENTER);
        centerPanel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_END);

        JPanel rightPanel = new JPanel();
        rightPanel.add(new JLabel("right"));

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(rightPanel, BorderLayout.LINE_END);
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TestDemo::new);
    }
}
