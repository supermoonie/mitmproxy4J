package com.github.supermoonie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.constant.EnumYesNo;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
@Service
@Slf4j
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Override
    public Integer changeRecordStatus() {
        return null;
    }

    @Override
    public void initial() {
        initRecordStatus();
    }

    private void initRecordStatus() {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", RECORD_KEY);
        Config config = configMapper.selectOne(queryWrapper);
        if (null == config) {
            config = new Config();
            config.setId(UUID.randomUUID().toString());
            config.setKey(RECORD_KEY);
            config.setValue(String.valueOf(EnumYesNo.YES));
            configMapper.insert(config);
        }
    }
}
