package com.github.supermoonie.proxy.swing;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * @author supermoonie
 * @date 2020-11-21
 */
public class ApplicationPreferences {

    private final static Logger log = LoggerFactory.getLogger(ApplicationPreferences.class);

    public static final String KEY_IS_DARK_THEME = "isDarkTheme";
    public static final String KEY_CLOSE_AFTER_SEND = "closeAfterSend";

    private static Preferences state;

    public static void init(String rootPath) {
        state = Preferences.userRoot().node(rootPath);
    }

    public static void initLaf() {
        // set look and feel
        try {
            boolean isDarkTheme = state.getBoolean(KEY_IS_DARK_THEME, false);
            ThemeManager.install(isDarkTheme);
            if (isDarkTheme) {
                ThemeManager.setDarkLookFeel();
            } else {
                ThemeManager.setLightLookFeel();
            }
            Locale.setDefault(Locale.ENGLISH);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Preferences getState() {
        return state;
    }
}
