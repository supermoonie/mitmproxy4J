package com.github.supermoonie.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.constant.EnumConfigType;
import com.github.supermoonie.controller.params.ConfigSetting;
import com.github.supermoonie.controller.params.ThrottlingSetting;
import com.github.supermoonie.mapper.ConfigMapper;
import com.github.supermoonie.model.Config;
import com.github.supermoonie.runner.InternalProxyRunner;
import com.github.supermoonie.service.ConfigService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    @Resource
    private InternalProxyRunner internalProxyRunner;

    @GetMapping("/get/remoteUriMap")
    public ResponseEntity<List<Config>> getRemoteUriMap() {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<Config>().eq("type", EnumConfigType.REMOTE_URI_MAP.getType());
        List<Config> configs = configMapper.selectList(queryWrapper);
        return ResponseEntity.ok(configs);
    }

    @PostMapping("/set/remoteUriMap")
    public ResponseEntity<List<Config>> setRemoteUriMap(@RequestBody List<ConfigSetting> remoteUriMapList) {
        QueryWrapper<Config> configQueryWrapper = new QueryWrapper<Config>().eq("type", EnumConfigType.REMOTE_URI_MAP.getType());
        configMapper.delete(configQueryWrapper);
        remoteUriMapList.forEach(setting -> {
            Config config = new Config();
            config.setKey(setting.getKey());
            config.setValue(setting.getValue());
            config.setType(EnumConfigType.REMOTE_URI_MAP.getType());
            QueryWrapper<Config> queryWrapper = new QueryWrapper<Config>().eq("key", setting.getKey()).eq("type", EnumConfigType.REMOTE_URI_MAP.getType());
            Config conf = configMapper.selectOne(queryWrapper);
            if (null != conf) {
                config.setId(conf.getId());
                configMapper.updateById(config);
            } else {
                config.setId(UUID.randomUUID().toString());
                configMapper.insert(config);
            }
        });
        List<Config> configs = configMapper.selectList(configQueryWrapper);
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/proxy/port")
    public ResponseEntity<Integer> proxyPort() {
        return ResponseEntity.ok(internalProxyRunner.getProxy().getPort());
    }

    @PostMapping("/proxy/setting")
    public ResponseEntity<Boolean> proxySetting(@RequestParam("port") Integer port) {
        if (port < 1024) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
        internalProxyRunner.restart(port);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @PostMapping("/throttling/setting")
    public ResponseEntity<String> throttlingSetting(@RequestBody ThrottlingSetting setting) {
        return ResponseEntity.ok(configService.throttlingSetting(setting));
    }

    @PostMapping("/switch/throttling")
    public ResponseEntity<String> switchThrottling() {
        return ResponseEntity.ok(configService.switchThrottling());
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

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status(@RequestParam("keys") List<String> keys) {
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        for (String key : keys) {
            queryWrapper.or().eq("key", key);
        }
        List<Config> configs = configMapper.selectList(queryWrapper);
        Map<String, String> map = configs.stream().collect(Collectors.toMap(Config::getKey, Config::getValue));
        return ResponseEntity.ok(map);
    }
}
