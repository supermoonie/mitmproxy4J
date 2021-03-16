package com.github.supermoonie.proxy.fx.setting;

import com.github.supermoonie.proxy.fx.util.JSON;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class GlobalSettingTest {

    @Test
    public void test() {
        GlobalSetting setting = GlobalSetting.getInstance();
        System.out.println(JSON.toJsonString(setting));
        GlobalSetting globalSetting = JSON.parse(JSON.toJsonString(setting), GlobalSetting.class);
        System.out.println(JSON.toJsonString(globalSetting));
    }

}