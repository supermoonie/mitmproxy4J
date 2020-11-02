package com.github.supermoonie.proxy.fx.util;

import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class SettingUtil {

    private static final Logger log = LoggerFactory.getLogger(SettingUtil.class);

    private SettingUtil() {}

    public static void load() {
        File settingHome = new File(System.getProperty("user.home") + File.separator + ".mitmproxy4J");
        if (!settingHome.exists() && !settingHome.mkdir()) {
            AlertUtil.warning("Fail make setting home!");
            return;
        }
        File settingFile = new File(settingHome.getAbsolutePath() + File.separator + "global_settings.json");
        if (!settingFile.exists()) {
            return;
        }
        try {
            String settings = FileUtils.readFileToString(settingFile, StandardCharsets.UTF_8);
            GlobalSetting.setInstance(JSON.parse(settings, GlobalSetting.class));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }

    public static void save(GlobalSetting globalSetting) {
        File settingHome = new File(System.getProperty("user.home") + File.separator + ".mitmproxy4J");
        if (!settingHome.exists() && !settingHome.mkdir()) {
            AlertUtil.warning("Fail make setting home!");
        }
        File settingFile = new File(settingHome.getAbsolutePath() + File.separator + "global_settings.json");
        try {
            FileUtils.writeStringToFile(settingFile, JSON.toJsonString(globalSetting), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }
}
