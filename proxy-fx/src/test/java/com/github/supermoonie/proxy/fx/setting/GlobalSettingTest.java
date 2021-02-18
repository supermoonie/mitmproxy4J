package com.github.supermoonie.proxy.fx.setting;

import com.github.supermoonie.proxy.fx.util.JacksonUtil;
import org.junit.Test;

/**
 * @author supermoonie
 * @since 2020/11/2
 */
public class GlobalSettingTest {

    @Test
    public void test() {
        GlobalSetting setting = GlobalSetting.getInstance();
        System.out.println(JacksonUtil.toJsonString(setting));
        GlobalSetting globalSetting = JacksonUtil.parse(JacksonUtil.toJsonString(setting), GlobalSetting.class);
        System.out.println(JacksonUtil.toJsonString(globalSetting));
    }

}