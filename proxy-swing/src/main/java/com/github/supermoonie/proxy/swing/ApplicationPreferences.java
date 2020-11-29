package com.github.supermoonie.proxy.swing;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.util.prefs.Preferences;

/**
 * @author supermoonie
 * @date 2020-11-21
 */
public class ApplicationPreferences {

    public static final String KEY_LAF = "laf";
    public static final String KEY_LAF_THEME = "lafTheme";

    public static final String RESOURCE_PREFIX = "res:";
    public static final String FILE_PREFIX = "file:";

    public static final String THEME_UI_KEY = "__FlatLaf.proxy.theme";

    private static Preferences state;

    public static void init(String rootPath) {
        state = Preferences.userRoot().node(rootPath);
    }

    public static void initLaf(String[] args) {
        // set look and feel
        try {
            if (args.length > 0) {
                UIManager.setLookAndFeel(args[0]);
            } else {
                FlatDarkLaf.install();
                FlatLightLaf.install();
                String lafClassName = state.get(KEY_LAF, FlatLightLaf.class.getName());
                UIManager.setLookAndFeel(lafClassName);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            // fallback
            FlatLightLaf.install();
        }

        // remember active look and feel
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                state.put(KEY_LAF, UIManager.getLookAndFeel().getClass().getName());
            }
        });
    }
}
