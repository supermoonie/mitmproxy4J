package com.github.supermoonie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.controller.params.ThrottlingSetting;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.service.ConfigService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
@Api
@RestController
@RequestMapping(value = "/config")
@CrossOrigin
@Slf4j
@Validated
public class ConfigController {

    @Resource
    private ConfigService configService;

    @Resource
    private ConfigMapper configMapper;

    @PostMapping("/throttling/setting")
    public ResponseEntity<String> throttlingSetting(@RequestBody ThrottlingSetting setting) {
        return ResponseEntity.ok(configService.throttlingSetting(setting));
    }

    @PostMapping("/{key}/change")
    public ResponseEntity<String> change(@PathVariable("key") String key) {
        return ResponseEntity.ok(configService.change(key));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> all() {
        List<Config> configs = configMapper.selectList(new QueryWrapper<>());
        Map<String, String> map = configs.stream().collect(Collectors.toMap(Config::getKey, Config::getValue));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/{key}/status")
    public ResponseEntity<String> status(@PathVariable("key") String key) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("key", key);
        Config config = configMapper.selectOne(queryWrapper);
        return ResponseEntity.ok(config.getValue());
    }
}
