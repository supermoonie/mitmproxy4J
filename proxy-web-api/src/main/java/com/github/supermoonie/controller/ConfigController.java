package com.github.supermoonie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.service.ConfigService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
@Api
@RestController
@RequestMapping(value = "/config")
@CrossOrigin
@Slf4j
public class ConfigController {

    @Resource
    private ConfigService configService;

    @Resource
    private ConfigMapper configMapper;

    @PostMapping("/{key}/change")
    public ResponseEntity<String> change(@PathVariable("key") String key) {
        return ResponseEntity.ok(configService.change(key));
    }

    @GetMapping("/{key}/status")
    public ResponseEntity<String> status(@PathVariable("key") String key) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", key);
        Config config = configMapper.selectOne(queryWrapper);
        return ResponseEntity.ok(config.getValue());
    }
}
