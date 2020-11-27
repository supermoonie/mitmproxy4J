package com.github.supermoonie.proxy.swing;

import javax.swing.*;
import java.net.URL;

/**
 * @author supermoonie
 * @since 2020/11/27
 */
public class AnimatedIconTreeExample {

    public static void main(String[] args) {

        URL url = AnimatedIconTreeExample.class.getResource("/com/github/supermoonie/proxy/swing/icon/25.gif");
        Icon icon = new ImageIcon(url);
        JLabel label = new JLabel(icon);

        JFrame f = new JFrame("Animation");
        f.getContentPane().add(label);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
