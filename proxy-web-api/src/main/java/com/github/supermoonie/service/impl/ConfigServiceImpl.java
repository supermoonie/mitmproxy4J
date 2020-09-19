package com.github.supermoonie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.constant.EnumYesNo;
import com.github.supermoonie.controller.params.ThrottlingSetting;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.proxy.InternalProxy;
import com.github.supermoonie.runner.InternalProxyRunner;
import com.github.supermoonie.service.ConfigService;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
@Service
@Slf4j
@Transactional(rollbackFor = RuntimeException.class)
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private InternalProxyRunner internalProxyRunner;

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

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public String throttlingSetting(ThrottlingSetting setting) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", THROTTLING_KEY);
        Config config = configMapper.selectOne(queryWrapper);
        String value = setting.getStatus() ? EnumYesNo.YES.toString() : EnumYesNo.NO.toString();
        config.setValue(value);
        configMapper.updateById(config);
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", THROTTLING_READ_LIMIT);
        config = configMapper.selectOne(queryWrapper);
        config.setValue(setting.getReadLimit().toString());
        configMapper.updateById(config);
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", THROTTLING_WRITE_LIMIT);
        config = configMapper.selectOne(queryWrapper);
        config.setValue(setting.getWriteLimit().toString());
        configMapper.updateById(config);
        InternalProxy proxy = internalProxyRunner.getProxy();
        proxy.setTrafficShaping(setting.getStatus());
        GlobalChannelTrafficShapingHandler handler = proxy.getTrafficShapingHandler();
        if (setting.getStatus()) {
            handler.setReadLimit(setting.getReadLimit());
            handler.setWriteLimit(setting.getWriteLimit());
        }
        return value;
    }

    @Override
    public String switchThrottling() {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", THROTTLING_KEY);
        Config config = configMapper.selectOne(queryWrapper);
        if (config.getValue().equals(EnumYesNo.YES.toString())) {
            config.setValue(EnumYesNo.NO.toString());
        } else {
            config.setValue(EnumYesNo.YES.toString());
        }
        configMapper.updateById(config);
        if (config.getValue().equals(EnumYesNo.YES.toString())) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("key", THROTTLING_READ_LIMIT).or().eq("key", THROTTLING_WRITE_LIMIT);
            List<Config> configs = configMapper.selectList(queryWrapper);
            Map<String, String> map = configs.stream().collect(Collectors.toMap(Config::getKey, Config::getValue));
            GlobalChannelTrafficShapingHandler trafficShapingHandler = internalProxyRunner.getProxy().getTrafficShapingHandler();
            trafficShapingHandler.setReadLimit(Long.parseLong(map.get(THROTTLING_READ_LIMIT)));
            trafficShapingHandler.setWriteLimit(Long.parseLong(map.get(THROTTLING_WRITE_LIMIT)));
            internalProxyRunner.getProxy().setTrafficShaping(true);
        } else {
            internalProxyRunner.getProxy().setTrafficShaping(false);
        }
        return config.getValue();
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
