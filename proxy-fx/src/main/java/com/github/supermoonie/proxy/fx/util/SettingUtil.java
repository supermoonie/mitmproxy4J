package com.github.supermoonie.proxy.fx.util;

import com.github.supermoonie.proxy.fx.setting.GlobalSetting;
import com.github.supermoonie.proxy.fx.setting.SerializeGlobalSetting;
import javafx.collections.FXCollections;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

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
            SerializeGlobalSetting globalSetting = JSON.parse(settings, SerializeGlobalSetting.class);
            GlobalSetting instance = GlobalSetting.getInstance();
            instance.setRecord(Objects.requireNonNullElse(globalSetting.getRecord(), true));
            instance.setPort(Objects.requireNonNullElse(globalSetting.getPort(), 10801));
            instance.setAuth(Objects.requireNonNullElse(globalSetting.getAuth(), false));
            instance.setUsername(globalSetting.getUsername());
            instance.setPassword(globalSetting.getPassword());
            instance.setSystemProxy(Objects.requireNonNullElse(globalSetting.getSystemProxy(), false));
            instance.setThrottling(Objects.requireNonNullElse(globalSetting.getThrottling(), false));
            instance.setThrottlingWriteLimit(Objects.requireNonNullElse(globalSetting.getThrottlingWriteLimit(), 320L));
            instance.setThrottlingReadLimit(Objects.requireNonNullElse(globalSetting.getThrottlingReadLimit(), 640L));
            instance.setBlockUrl(Objects.requireNonNullElse(globalSetting.getBlockUrl(), false));
            instance.setBlockUrlList(FXCollections.observableSet(Objects.requireNonNullElse(globalSetting.getBlockUrlList(), new HashSet<>())));
            instance.setAllowUrl(Objects.requireNonNullElse(globalSetting.getAllowUrl(), false));
            instance.setAllowUrlList(FXCollections.observableSet(Objects.requireNonNullElse(globalSetting.getAllowUrlList(), new HashSet<>())));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AlertUtil.error(e);
        }
    }

    public synchronized static void save(GlobalSetting globalSetting) {
        File settingHome = new File(System.getProperty("user.home") + File.separator + ".mitmproxy4J");
        if (!settingHome.exists() && !settingHome.mkdir()) {
            AlertUtil.warning("Fail make setting home!");
        }
        File settingFile = new File(settingHome.getAbsolutePath() + File.separator + "global_settings.json");
        try {
            FileUtils.writeStringToFile(settingFile, JSON.toJsonString(globalSetting), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
