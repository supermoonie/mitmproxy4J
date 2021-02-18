package com.github.supermoonie.proxy.fx.tray;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.util.SettingUtil;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author supermoonie
 * @since 2020/9/24
 */
public class SystemTrayManager {

    public static final String MAIN_TRAY = "MAIN";

    private static final Map<String, TrayIconWrapper> TRAY_ICON_WRAPPER_MAP = new ConcurrentHashMap<>();

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
        TrayIconWrapper primaryTray = new TrayIconWrapper(MAIN_TRAY, icon);
        PopupMenu popupMenu = new PopupMenu();
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.addActionListener(event -> {
            destroy();
            Platform.runLater(() -> {
                SettingUtil.save(GlobalSetting.getInstance());
                App.EXECUTOR.shutdown();
                Platform.exit();
                System.exit(0);
            });
        });
        popupMenu.add(quitItem);
        primaryTray.setPopupMenu(popupMenu);
        systemTray.add(primaryTray);
        TRAY_ICON_WRAPPER_MAP.put(primaryTray.getName(), primaryTray);
    }

    public static void destroy() {
        SystemTray systemTray = SystemTray.getSystemTray();
        if (!SystemTray.isSupported()) {
            return;
        }
        TRAY_ICON_WRAPPER_MAP.forEach((name, trayIcon) -> systemTray.remove(trayIcon));
    }
}
