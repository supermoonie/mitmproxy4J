package com.github.supermoonie.proxy.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.prefs.Preferences;

/**
 * @author supermoonie
 * @date 2020-11-21
 */
public class AppPreferences {

    private final static Logger log = LoggerFactory.getLogger(AppPreferences.class);

    public static final String KEY_LOCAL_VERSION = "localVersion";
    public static final String KEY_IS_DARK_THEME = "isDarkTheme";
    public static final String KEY_CLOSE_AFTER_SEND = "closeAfterSend";
    public static final String KEY_FONT_FAMILY = "fontFamily";
    public static final String KEY_FONT_SIZE = "fontSize";
    public static final String KEY_PROXY_PORT = "proxyPort";
    public static final String KEY_PROXY_AUTH = "proxyAuth";
    public static final String KEY_PROXY_AUTH_USER = "proxyAuthUser";
    public static final String KEY_PROXY_AUTH_PWD = "proxyAuthPwd";
    public static final String KEY_PROXY_LIMIT_ENABLE = "proxyLimit";
    public static final String KEY_PROXY_LIMIT_WRITE = "proxyWriteLimit";
    public static final String KEY_PROXY_LIMIT_READ = "proxyReadLimit";
    public static final String KEY_ALLOW_LIST_ENABLE = "allowListEnable";
    public static final String KEY_BLOCK_LIST_ENABLE = "blockListEnable";
    public static final String KEY_REMOTE_MAP_ENABLE = "remoteMapEnable";
    public static final String KEY_LOCAL_MAP_ENABLE = "localMapEnable";
    public static final String KEY_EXTERNAL_PROXY_ENABLE = "externalProxy";
    public static final String KEY_EXTERNAL_PROXY_BYPASS_LIST = "externalProxyByPassList";
    public static final String KEY_EXTERNAL_PROXY_BYPASS_LOCALHOST = "externalProxyByPassLocalHost";
    public static final String KEY_DNS_ENABLE = "dnsEnable";
    public static final String KEY_DNS_LOCAL_HOST_ENABLE = "sysHostEnable";

    public static final int DEFAULT_LOCAL_VERSION = 4;
    public static final String DEFAULT_FONT_FAMILY = "Helvetica Neue";
    public static final int DEFAULT_FONT_SIZE = 13;
    public static final int DEFAULT_PROXY_PORT = 10801;
    public static final boolean DEFAULT_PROXY_AUTH = false;
    public static final boolean DEFAULT_ALLOW_LIST_ENABLE = false;
    public static final boolean DEFAULT_BLOCK_LIST_ENABLE = false;
    public static final boolean DEFAULT_REMOTE_MAP_ENABLE = false;
    public static final boolean DEFAULT_LOCAL_MAP_ENABLE = false;
    public static final long DEFAULT_PROXY_LIMIT_WRITE = 320_000L;
    public static final long DEFAULT_PROXY_LIMIT_READ = 640_000L;
    public static final boolean DEFAULT_EXTERNAL_PROXY_ENABLE = false;
    public static final boolean DEFAULT_EXTERNAL_PROXY_BYPASS_LOCALHOST = true;
    public static final boolean DEFAULT_DNS_ENABLE = false;
    public static final boolean DEFAULT_DNS_LOCAL_HOST_ENABLE = true;

    private static Preferences state;

    public static void init(String rootPath) {
        state = Preferences.userRoot().node(rootPath);
    }

    public static Font getFont() {
        String family = state.get(KEY_FONT_FAMILY, DEFAULT_FONT_FAMILY);
        int fontSize = state.getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        return new Font(family, Font.PLAIN, fontSize);
    }

    public static Preferences getState() {
        return state;
    }

}
