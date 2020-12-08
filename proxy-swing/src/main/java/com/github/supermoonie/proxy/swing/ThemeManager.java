package com.github.supermoonie.proxy.swing;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.github.supermoonie.proxy.swing.ApplicationPreferences.KEY_IS_DARK_THEME;

/**
 * @author supermoonie
 * @since 2020/12/5
 */
public class ThemeManager {

    private static Theme codeAreaLightTheme;
    private static Theme codeAreaDarkTheme;
    private static boolean dark;

    /**
     * 加载主题
     */
    public static void install() {
        FlatDarkLaf.install();
        FlatLightLaf.install();
        try {
            codeAreaLightTheme = Theme.load(ThemeManager.class.getResourceAsStream("/com/github/supermoonie/proxy/swing/light.xml"));
            codeAreaDarkTheme = Theme.load(ThemeManager.class.getResourceAsStream("/com/github/supermoonie/proxy/swing/dark.xml"));
        } catch (IOException ignore) {
            // never
        }
        // remember active look and feel
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                boolean isDark = ((FlatLaf) UIManager.getLookAndFeel()).isDark();
                ApplicationPreferences.getState().putBoolean(KEY_IS_DARK_THEME, isDark);
                if (isDark) {
                    codeAreaDarkTheme.apply(Application.MAIN_FRAME.getRequestJsonArea());
                    codeAreaDarkTheme.apply(Application.MAIN_FRAME.getResponseCodeArea());
                } else {
                    codeAreaLightTheme.apply(Application.MAIN_FRAME.getRequestJsonArea());
                    codeAreaLightTheme.apply(Application.MAIN_FRAME.getResponseCodeArea());
                }
            }
        });
    }

    /**
     * 设置明亮主题
     */
    public static void setLightLookFeel() {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            try {
                UIManager.setLookAndFeel(FlatLightLaf.class.getName());
                dark = false;
            } catch (Exception ignore) {

            }
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    /**
     * 设置黑暗主题
     */
    public static void setDarkLookFeel() {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            try {
                UIManager.setLookAndFeel(FlatDarkLaf.class.getName());
                dark = true;
            } catch (Exception ignore) {

            }
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    public static boolean isDark() {
        return dark;
    }
}
