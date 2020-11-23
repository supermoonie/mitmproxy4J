package com.github.supermoonie.proxy.swing;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.proxy.swing.gui.ProxyFrame;
import com.github.supermoonie.proxy.swing.gui.Splash;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 *
 * @author supermoonie
 */
public class Application {

    private static final String PREFS_ROOT_PATH = "/proxy-swing";

    public static void main(String[] args) {
        // on macOS enable screen menu bar
        if (SystemInfo.isMacOS && System.getProperty("apple.laf.useScreenMenuBar") == null) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        SwingUtilities.invokeLater(() -> {
            ApplicationPreferences.init(PREFS_ROOT_PATH);
            // enable window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            // application specific UI defaults
            FlatLaf.registerCustomDefaultsSource("com.github.supermoonie.proxy.swing");
            // set look and feel
            ApplicationPreferences.initLaf(args);
            // install inspectors
            FlatInspector.install("ctrl shift alt X");
            FlatUIDefaultsInspector.install("ctrl shift alt Y");

            ProxyFrame frame = new ProxyFrame();
            frame.setPreferredSize(new Dimension(1280, 620));
            // show frame
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
