package com.github.supermoonie.proxy.swing.gui;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.IOException;

/**
 * @author supermoonie
 * @since 2020/11/22
 */
public class Splash {

    private static final JWindow WINDOW = new JWindow();

    public static void show() {
        try {
            WINDOW.getContentPane().add(
                    new JLabel("", new ImageIcon(IOUtils.resourceToByteArray("/image/splash.gif")), SwingConstants.CENTER));
        } catch (IOException ignore) {

        }
        WINDOW.setAlwaysOnTop(true);
        WINDOW.setLocationRelativeTo(null);
        WINDOW.setSize(300, 200);
        WINDOW.setVisible(true);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public static void hide() {
        WINDOW.setVisible(false);
    }

}
