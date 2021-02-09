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
    public static void install(boolean dark) {
        FlatDarkLaf.install();
        FlatLightLaf.install();
        try {
            codeAreaLightTheme = Theme.load(ThemeManager.class.getResourceAsStream("/com/github/supermoonie/proxy/swing/light.xml"));
            codeAreaDarkTheme = Theme.load(ThemeManager.class.getResourceAsStream("/com/github/supermoonie/proxy/swing/dark.xml"));
        } catch (IOException ignore) {
            // never
        }
        ThemeManager.dark = dark;
        // remember active look and feel
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                boolean isDark = ((FlatLaf) UIManager.getLookAndFeel()).isDark();
                ApplicationPreferences.getState().putBoolean(KEY_IS_DARK_THEME, isDark);
                if (isDark) {
                    codeAreaDarkTheme.apply(MitmProxy4J.MAIN_FRAME.getRequestJsonArea());
                    codeAreaDarkTheme.apply(MitmProxy4J.MAIN_FRAME.getResponseCodeArea());
                } else {
                    codeAreaLightTheme.apply(MitmProxy4J.MAIN_FRAME.getRequestJsonArea());
                    codeAreaLightTheme.apply(MitmProxy4J.MAIN_FRAME.getResponseCodeArea());
                }
            }
        });
    }

    /**
     * 设置明亮主题
     */
    public static void setLightLookFeel() {
        FlatAnimatedLafChange.showSnapshot();
        try {
            UIManager.setLookAndFeel(FlatLightLaf.class.getName());
            dark = false;
        } catch (Exception ignore) {

        }
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    /**
     * 设置黑暗主题
     */
    public static void setDarkLookFeel() {
        FlatAnimatedLafChange.showSnapshot();
        try {
            UIManager.setLookAndFeel(FlatDarkLaf.class.getName());
            dark = true;
        } catch (Exception ignore) {

        }
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void setFont(String fontFamily, int fontSize) {
        FlatAnimatedLafChange.showSnapshot();
        Font font = UIManager.getFont("defaultFont");
        Font newFont = new Font(fontFamily, font.getStyle(), fontSize);
        UIManager.put("defaultFont", newFont);
        SwingUtilities.invokeLater(() -> {
            MitmProxy4J.MAIN_FRAME.getRequestJsonArea().setFont(newFont);
            MitmProxy4J.MAIN_FRAME.getResponseCodeArea().setFont(newFont);
            MitmProxy4J.MAIN_FRAME.getRequestJsonArea().revalidate();
            MitmProxy4J.MAIN_FRAME.getResponseCodeArea().revalidate();
        });
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static boolean isDark() {
        return dark;
    }

    public static Theme getCodeAreaLightTheme() {
        return codeAreaLightTheme;
    }

    public static Theme getCodeAreaDarkTheme() {
        return codeAreaDarkTheme;
    }
}
