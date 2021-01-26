package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.gui.panel.ExternalProxyDialog;

import java.awt.*;

/**
 * @author supermoonie
 * @since 2021/1/26
 */
public class ExternalProxyDialogController extends ExternalProxyDialog {


    public ExternalProxyDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getEnableCheckBox().addActionListener(e -> {
            getProxyTable().setEnabled(getEnableCheckBox().isSelected());
            getAddButton().setEnabled(getEnableCheckBox().isSelected());
            getRemoveButton().setEnabled(getEnableCheckBox().isSelected());
        });
    }
}
