package com.github.supermoonie.proxy.swing;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.gui.MainFrame;
import com.github.supermoonie.proxy.swing.icon.SvgIcons;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.github.supermoonie.proxy.swing.proxy.intercept.InternalProxyInterceptInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

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
            Thread.setDefaultUncaughtExceptionHandler(Application::showError);
            try {
                DaoCollections.init();
            } catch (SQLException e) {
                showError(e);
                return;
            }
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
            int port = ApplicationPreferences.getState().getInt(ApplicationPreferences.KEY_PROXY_PORT, ApplicationPreferences.VALUE_DEFAULT_PROXY_PORT);
            boolean auth = ApplicationPreferences.getState().getBoolean(ApplicationPreferences.KEY_PROXY_AUTH, ApplicationPreferences.VALUE_DEFAULT_PROXY_AUTH);
            String username = ApplicationPreferences.getState().get(ApplicationPreferences.KEY_PROXY_AUTH_USER, "");
            String password = ApplicationPreferences.getState().get(ApplicationPreferences.KEY_PROXY_AUTH_PWD, "");
            ProxyManager.start(port, auth, username, password, new InternalProxyInterceptInitializer());
            ProxyManager.getInternalProxy().getTrafficShapingHandler().setWriteChannelLimit(80);
            ProxyManager.getInternalProxy().getTrafficShapingHandler().setReadChannelLimit(80);
            MAIN_FRAME = new MainFrame();
            MAIN_FRAME.setPreferredSize(new Dimension(1280, 800));
            // show frame
            MAIN_FRAME.pack();
            MAIN_FRAME.setLocationRelativeTo(null);
            MAIN_FRAME.setVisible(true);
            MAIN_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }

    public static void showError(String errorMessage) {
        showError(errorMessage, "Error!");
    }

    public static void showError(Thread thread, Throwable exception) {
        log.error(exception.getMessage(), exception);
        String errorMessage =
                "Thread: " + thread.getName()
                        + "\nMessage: " + exception.getMessage()
                        + "\nStackTrace: " + List.of(exception.getStackTrace()).stream().map(StackTraceElement::toString).collect(Collectors.joining("\n"));
        String title = exception.getClass().getName();
        showError(title, errorMessage);
    }

    public static void showError(Throwable exception) {
        log.error(exception.getMessage(), exception);
        String errorMessage = "Message: " + exception.getMessage()
                + "\nStackTrace: " + List.of(exception.getStackTrace()).stream().map(StackTraceElement::toString).collect(Collectors.joining("\n"));
        String title = exception.getClass().getName();
        showError(title, errorMessage);
    }

    public static void showError(String title, String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, title, JOptionPane.ERROR_MESSAGE);
    }
}
