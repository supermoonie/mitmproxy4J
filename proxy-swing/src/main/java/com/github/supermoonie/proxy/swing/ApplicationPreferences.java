package com.github.supermoonie.proxy.swing;

import com.github.supermoonie.proxy.swing.dao.DaoCollections;
import com.github.supermoonie.proxy.swing.entity.AccessControl;
import com.j256.ormlite.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

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
    public static final String KEY_PROXY_PORT = "proxyPort";
    public static final String KEY_PROXY_AUTH = "proxyAuth";
    public static final String KEY_PROXY_AUTH_USER = "proxyAuthUser";
    public static final String KEY_PROXY_AUTH_PWD = "proxyAuthPwd";
    public static final String KEY_ALLOW_LIST_ENABLE = "allowListEnable";
    public static final String KEY_BLOCK_LIST_ENABLE = "blockListEnable";
    public static final String KEY_REMOTE_MAP_ENABLE = "remoteMapEnable";
    public static final String KEY_LOCAL_MAP_ENABLE = "localMapEnable";

    public static final String VALUE_DEFAULT_FONT_FAMILY = "Helvetica Neue";
    public static final int VALUE_DEFAULT_FONT_SIZE = 14;
    public static final int VALUE_DEFAULT_PROXY_PORT = 10801;
    public static final boolean VALUE_DEFAULT_PROXY_AUTH = false;
    public static final boolean VALUE_ALLOW_LIST_ENABLE = false;
    public static final boolean VALUE_BLOCK_LIST_ENABLE = false;
    public static final boolean VALUE_REMOTE_MAP_ENABLE = false;
    public static final boolean VALUE_LOCAL_MAP_ENABLE = false;

    private static Preferences state;
    private static Set<String> accessControl = new HashSet<>();

    public static void init(String rootPath) {
        state = Preferences.userRoot().node(rootPath);
        try {
            Dao<AccessControl, Integer> accessDao = DaoCollections.getDao(AccessControl.class);
            List<AccessControl> accessControls = accessDao.queryForAll();
            if (null != accessControls && accessControls.size() > 0) {
                accessControls.forEach(ac -> accessControl.add(ac.getAccessIp()));
            }
        } catch (SQLException e) {
            Application.showError(e);
        }
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
            Application.showError(e);
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

    public static Set<String> getAccessControl() {
        return accessControl;
    }

    public static void setAccessControl(Set<String> accessControl) {
        ApplicationPreferences.accessControl = accessControl;
        Dao<AccessControl, Integer> accessDao = DaoCollections.getDao(AccessControl.class);
        try {
            accessDao.deleteBuilder().delete();
            List<AccessControl> list = accessControl.stream().map(ip -> {
                AccessControl ac = new AccessControl();
                ac.setAccessIp(ip);
                ac.setTimeCreated(new Date());
                return ac;
            }).collect(Collectors.toList());
            accessDao.create(list);
        } catch (SQLException e) {
            Application.showError(e);
        }
    }

}
