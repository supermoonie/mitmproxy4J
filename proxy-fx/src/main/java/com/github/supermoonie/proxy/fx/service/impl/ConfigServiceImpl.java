package com.github.supermoonie.proxy.fx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.constant.EnumYesNo;
import com.github.supermoonie.proxy.fx.entity.Config;
import com.github.supermoonie.proxy.fx.mapper.ConfigMapper;
import com.github.supermoonie.proxy.fx.service.ConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Override
    public String change(String key) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", key);
        Config config = configMapper.selectOne(queryWrapper);
        if (null == config) {
            config = new Config();
            config.setId(UUID.randomUUID().toString());
            config.setKey(key);
            config.setValue(String.valueOf(EnumYesNo.YES));
            configMapper.insert(config);
            return EnumYesNo.YES.toString();
        } else {
            if (config.getValue().equals(String.valueOf(EnumYesNo.YES.getValue()))) {
                config.setValue(String.valueOf(EnumYesNo.NO.getValue()));
            } else {
                config.setValue(String.valueOf(EnumYesNo.YES.getValue()));
            }
            configMapper.updateById(config);
            return config.getValue();
        }
    }


    @Override
    public String switchThrottling() {
        return "";
    }

    @Override
    public void initial() {
        initStatus(THROTTLING_KEY, EnumYesNo.NO.toString());
        initStatus(RECORD_KEY, EnumYesNo.YES.toString());
        initStatus(THROTTLING_READ_LIMIT, "64");
        initStatus(THROTTLING_WRITE_LIMIT, "32");
    }

    private void initStatus(String key, String value) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", key);
        Config config = configMapper.selectOne(queryWrapper);
        if (null == config) {
            config = new Config();
            config.setId(UUID.randomUUID().toString());
            config.setKey(key);
            config.setValue(value);
            configMapper.insert(config);
        }
    }
}
