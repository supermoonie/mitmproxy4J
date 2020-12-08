package com.github.supermoonie.proxy.swing;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.gui.MainFrame;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.github.supermoonie.proxy.swing.proxy.intercept.DefaultConfigIntercept;
import com.github.supermoonie.proxy.swing.proxy.intercept.DumpHttpRequestIntercept;
import com.github.supermoonie.proxy.swing.proxy.intercept.DumpHttpResponseIntercept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Hello world!
 *
 * @author supermoonie
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(5);

    public static final Date START_TIME = new Date();

    private static final String PREFS_ROOT_PATH = "/proxy-swing";

    public static MainFrame MAIN_FRAME;

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
            ApplicationPreferences.initLaf();
            // install inspectors
            FlatInspector.install("ctrl shift alt X");
            FlatUIDefaultsInspector.install("ctrl shift alt Y");
            try {
                DaoCollections.init();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE, new FlatSVGIcon("/com/github/supermoonie/proxy/swing/icon/lighting.svg"));
                return;
            }
            ProxyManager.start(10801, false, null, null, (requestIntercepts, responseIntercepts) -> {
                requestIntercepts.put("dumpHttpRequestIntercept", DumpHttpRequestIntercept.INSTANCE);
                responseIntercepts.put("dumpHttpResponseIntercept", DumpHttpResponseIntercept.INSTANCE);
                requestIntercepts.put("configurableIntercept", DefaultConfigIntercept.INSTANCE);
            });
            ProxyManager.getInternalProxy().getTrafficShapingHandler().setWriteChannelLimit(80);
            ProxyManager.getInternalProxy().getTrafficShapingHandler().setReadChannelLimit(80);
            MAIN_FRAME = new MainFrame();
            MAIN_FRAME.setPreferredSize(new Dimension(1280, 800));
            // show frame
            MAIN_FRAME.pack();
            MAIN_FRAME.setLocationRelativeTo(null);
            MAIN_FRAME.setVisible(true);
//            PROXY_FRAME.setExtendedState(PROXY_FRAME.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            MAIN_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
