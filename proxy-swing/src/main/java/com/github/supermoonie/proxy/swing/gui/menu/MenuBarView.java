package com.github.supermoonie.proxy.swing.gui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author supermoonie
 * @since 2021/1/12
 */
public class MenuBarView extends JMenuBar {

    private final JMenu proxyMenu = new JMenu("Proxy");
    private final JMenu toolsMenu = new JMenu("Tools");
    private final JMenu helpMenu = new JMenu("Help");

    /**
     * proxy menu items
     */
    private final JMenuItem systemProxyMenuItem = new JMenuItem("Enable System Proxy"){{
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }};
    private final JMenuItem proxySettingMenuItem = new JMenuItem("Proxy Settings...");
    private final JMenuItem accessSettingMenuItem = new JMenuItem("Access Control Settings...");
    private final JMenuItem throttlingMenuItem = new JMenuItem("Throttling Settings...");
    private final JMenuItem externalProxyMenuItem = new JMenuItem("External Proxy Settings...");

    /**
     * tools menu items
     */
    private final JMenuItem mapLocalMenuItem = new JMenuItem("Map Local..."){{
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }};
    private final JMenuItem mapRemoteMenuItem = new JMenuItem("Map Remote..."){{
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }};
    private final JMenuItem blockListMenuItem = new JMenuItem("Block List..."){{
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }};
    private final JMenuItem allowListMenuItem = new JMenuItem("Allow List..."){{
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }};
    private final JMenuItem composeMenuItem = new JMenuItem("Compose...");

    /**
     * help menu items
     */
    private final JMenuItem installCertMenuItem = new JMenuItem("Install Root Certificate");
    private final JMenuItem aboutMenuItem = new JMenuItem("About");

    public MenuBarView() {
        super();
        super.add(proxyMenu);
        super.add(toolsMenu);
        super.setHelpMenu(helpMenu);
        proxyMenu.add(systemProxyMenuItem);
        proxyMenu.add(proxySettingMenuItem);
        proxyMenu.add(accessSettingMenuItem);
        proxyMenu.add(throttlingMenuItem);
        proxyMenu.add(externalProxyMenuItem);
        toolsMenu.add(mapLocalMenuItem);
        toolsMenu.add(mapRemoteMenuItem);
        toolsMenu.add(blockListMenuItem);
        toolsMenu.add(allowListMenuItem);
        toolsMenu.add(composeMenuItem);
        helpMenu.add(installCertMenuItem);
        helpMenu.add(aboutMenuItem);
    }


    public JMenu getProxyMenu() {
        return proxyMenu;
    }

    public JMenu getToolsMenu() {
        return toolsMenu;
    }

    @Override
    public JMenu getHelpMenu() {
        return helpMenu;
    }

    public JMenuItem getSystemProxyMenuItem() {
        return systemProxyMenuItem;
    }

    public JMenuItem getProxySettingMenuItem() {
        return proxySettingMenuItem;
    }

    public JMenuItem getAccessSettingMenuItem() {
        return accessSettingMenuItem;
    }

    public JMenuItem getThrottlingMenuItem() {
        return throttlingMenuItem;
    }

    public JMenuItem getExternalProxyMenuItem() {
        return externalProxyMenuItem;
    }

    public JMenuItem getMapLocalMenuItem() {
        return mapLocalMenuItem;
    }

    public JMenuItem getMapRemoteMenuItem() {
        return mapRemoteMenuItem;
    }

    public JMenuItem getBlockListMenuItem() {
        return blockListMenuItem;
    }

    public JMenuItem getAllowListMenuItem() {
        return allowListMenuItem;
    }

    public JMenuItem getComposeMenuItem() {
        return composeMenuItem;
    }

    public JMenuItem getInstallCertMenuItem() {
        return installCertMenuItem;
    }

    public JMenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }
}
