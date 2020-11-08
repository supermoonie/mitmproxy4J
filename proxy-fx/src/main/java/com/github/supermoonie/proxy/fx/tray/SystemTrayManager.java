package com.github.supermoonie.proxy.fx.tray;

import com.github.supermoonie.proxy.fx.App;
import com.github.supermoonie.proxy.fx.proxy.ProxyManager;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
@Component
public class SystemTrayManager {

    @Resource
    private ApplicationContext applicationContext;

    public static final String MAIN_TRAY = "MAIN";

    private static final Map<String, TrayIconWrapper> TRAY_ICON_WRAPPER_MAP = new ConcurrentHashMap<>();

    public void init() throws AWTException, IOException {
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
            SpringApplication.exit(applicationContext, () -> 0);
            Platform.runLater(() -> {
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

    public void destroy() {
        SystemTray systemTray = SystemTray.getSystemTray();
        if (!SystemTray.isSupported()) {
            return;
        }
        TRAY_ICON_WRAPPER_MAP.forEach((name, trayIcon) -> systemTray.remove(trayIcon));
    }
}
