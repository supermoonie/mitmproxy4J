package com.github.supermoonie.proxy.swing.gui.panel.controller;

import com.github.supermoonie.proxy.swing.ApplicationPreferences;
import com.github.supermoonie.proxy.swing.ThemeManager;
import com.github.supermoonie.proxy.swing.gui.panel.AppearanceDialog;

import java.awt.*;
import java.awt.event.ItemListener;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2021/1/17
 */
public class AppearanceDialogController extends AppearanceDialog {

    public AppearanceDialogController(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        getThemeComboBox().addItemListener(e -> {
            int themeSelectedIndex = getThemeComboBox().getSelectedIndex();
            if (0 == themeSelectedIndex) {
                if (!ThemeManager.isDark()) {
                    ThemeManager.setDarkLookFeel();
                }
            } else {
                if (ThemeManager.isDark()) {
                    ThemeManager.setLightLookFeel();
                }
            }
        });
        ItemListener fontListener = e -> {
            String family = Objects.requireNonNullElse(getFontComboBox().getSelectedItem(), ApplicationPreferences.VALUE_DEFAULT_FONT_FAMILY).toString();
            family = "Default".equals(family) ? ApplicationPreferences.VALUE_DEFAULT_FONT_FAMILY : family;
            int fontSize = Integer.parseInt(Objects.requireNonNullElse(getFontSizeComboBox().getSelectedItem(), ApplicationPreferences.VALUE_DEFAULT_FONT_SIZE).toString());
            ThemeManager.setFont(family, fontSize);
            ApplicationPreferences.getState().put(ApplicationPreferences.KEY_FONT_FAMILY, family);
            ApplicationPreferences.getState().putInt(ApplicationPreferences.KEY_FONT_SIZE, fontSize);
        };
        getFontComboBox().addItemListener(fontListener);
        getFontSizeComboBox().addItemListener(fontListener);
    }
}
