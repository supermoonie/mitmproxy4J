package com.github.supermoonie.proxy.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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
    public static final String KEY_FONT_FAMILY = "fontFamily";
    public static final String KEY_FONT_SIZE = "fontSize";

    public static final String VALUE_DEFAULT_FONT_FAMILY = "Helvetica Neue";
    public static final int VALUE_DEFAULT_FONT_SIZE = 14;

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
            Font font = getFont();
            ThemeManager.setFont(font.getFamily(), font.getSize());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Font getFont() {
        String family = state.get(KEY_FONT_FAMILY, VALUE_DEFAULT_FONT_FAMILY);
        int fontSize = state.getInt(KEY_FONT_SIZE, VALUE_DEFAULT_FONT_SIZE);
        return new Font(family, Font.PLAIN, fontSize);
    }

    public static Preferences getState() {
        return state;
    }
}
