package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.gui.panel.ThrottlingDialog;
import com.github.supermoonie.proxy.swing.proxy.ProxyManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author supermoonie
 * @since 2021/1/23
 */
public class ThrottlingDialogController extends ThrottlingDialog {

    public ThrottlingDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addChangeListener(e -> {
            boolean enable = getEnableCheckBox().isSelected();
            getWriteLimitSpinner().setEnabled(enable);
            getReadLimitSpinner().setEnabled(enable);
        });
        getCancelButton().addActionListener(e -> setVisible(false));
        getOkButton().addActionListener(e -> {
            Object writeValue = getWriteLimitSpinner().getValue();
            if (null == writeValue) {
                JOptionPane.showMessageDialog(this, "Invalid Upload Value!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            long writeLimit;
            try {
                writeLimit = (long) (Double.parseDouble(writeValue.toString()) * 1_000);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Upload Value!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object readValue = getReadLimitSpinner().getValue();
            if (null == readValue) {
                JOptionPane.showMessageDialog(this, "Invalid Download Value!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            long readLimit;
            try {
                readLimit = (long) (Double.parseDouble(readValue.toString()) * 1_000);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Download Value!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean enable = getEnableCheckBox().isSelected();
            ProxyManager.enableLimit(enable);
            ProxyManager.setWriteLimit(writeLimit);
            ProxyManager.setReadLimit(readLimit);
            ApplicationPreferences.getState().putBoolean(ApplicationPreferences.KEY_PROXY_LIMIT_ENABLE, enable);
            ApplicationPreferences.getState().putLong(ApplicationPreferences.KEY_PROXY_LIMIT_WRITE, writeLimit);
            ApplicationPreferences.getState().putLong(ApplicationPreferences.KEY_PROXY_LIMIT_READ, readLimit);
            setVisible(false);
        });
    }
}
