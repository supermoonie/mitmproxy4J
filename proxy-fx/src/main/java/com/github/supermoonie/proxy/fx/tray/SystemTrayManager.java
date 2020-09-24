package com.github.supermoonie.proxy.fx.tray;

import com.sun.javafx.PlatformUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author supermoonie
 * @since 2020/9/24
 */
public class SystemTrayManager {

    public static void init() throws AWTException, IOException {
        SystemTray systemTray = SystemTray.getSystemTray();
        if (!SystemTray.isSupported()) {
            return;
        }
        URL iconUrl;
        if (PlatformUtil.isWindows()) {
            iconUrl = SystemTrayManager.class.getResource("/lightning-win.png");
        } else {
            iconUrl = SystemTrayManager.class.getResource("/lightning-mac.png");
        }
        BufferedImage icon = ImageIO.read(iconUrl);
        TrayIconWrapper primaryTray = new TrayIconWrapper("", icon);
        systemTray.add(primaryTray);
    }

    public static void destroy() {
        SystemTray systemTray = SystemTray.getSystemTray();
        if (!SystemTray.isSupported()) {
            return;
        }
        TrayIcon[] trayIcons = systemTray.getTrayIcons();
        for (TrayIcon trayIcon : trayIcons) {
            systemTray.remove(trayIcon);
        }
    }
}
