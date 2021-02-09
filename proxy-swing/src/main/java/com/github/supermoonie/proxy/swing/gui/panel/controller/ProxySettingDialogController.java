package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.MitmProxy4J;
import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.gui.panel.ProxySettingDialog;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;
import com.github.supermoonie.proxy.swing.proxy.intercept.InternalProxyInterceptInitializer;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2021/1/16
 */
public class ProxySettingDialogController extends ProxySettingDialog {

    public ProxySettingDialogController(Frame owner, String title, boolean modal, Integer port, Boolean auth, String user, String pwd) {
        super(owner, title, modal, port, auth, user, pwd);
        getCancelButton().addActionListener(e -> setVisible(false));
        getOkButton().addActionListener(e -> {
            Object portValue = getPortSpinner().getValue();
            if (null == portValue) {
                JOptionPane.showMessageDialog(this, "Invalid Port!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int proxyPort;
            try {
                proxyPort = Integer.parseInt(portValue.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Port!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean proxyAuth = getAuthCheckBox().isSelected();
            String username = Objects.requireNonNullElse(getUsernameTextField().getText(), "");
            String password = Objects.requireNonNullElse(getPasswordTextField().getText(), "");
            if (ProxyManager.getInternalProxy().getPort() != proxyPort) {
                boolean enableLimit = ProxyManager.getInternalProxy().isTrafficShaping();
                long writeLimit = ApplicationPreferences.getState().getLong(ApplicationPreferences.KEY_PROXY_LIMIT_WRITE, ApplicationPreferences.DEFAULT_PROXY_LIMIT_WRITE);
                long readLimit = ApplicationPreferences.getState().getLong(ApplicationPreferences.KEY_PROXY_LIMIT_READ, ApplicationPreferences.DEFAULT_PROXY_LIMIT_READ);
                ProxyManager.restart(proxyPort, proxyAuth, username, password, new InternalProxyInterceptInitializer());
                ProxyManager.enableLimit(enableLimit);
                ProxyManager.setWriteLimit(writeLimit);
                ProxyManager.setReadLimit(readLimit);
            } else {
                ProxyManager.getInternalProxy().setAuth(proxyAuth);
                ProxyManager.getInternalProxy().setUsername(username);
                ProxyManager.getInternalProxy().setPassword(password);
            }
            MitmProxy4J.MAIN_FRAME.setTitle("Lightning | Listening on " + ProxyManager.getInternalProxy().getPort());
            ApplicationPreferences.getState().putInt(ApplicationPreferences.KEY_PROXY_PORT, proxyPort);
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_PROXY_AUTH, proxyAuth);
            ApplicationPreferences.getState().put(ApplicationPreferences.KEY_PROXY_AUTH_USER, username);
            ApplicationPreferences.getState().put(ApplicationPreferences.KEY_PROXY_AUTH_PWD, password);
            setVisible(false);
        });
    }
}
