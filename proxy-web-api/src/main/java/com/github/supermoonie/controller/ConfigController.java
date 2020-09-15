package com.github.supermoonie.controller;

import com.github.supermoonie.service.ConfigService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/record")
    public ResponseEntity<Integer> record() {
        return ResponseEntity.ok(configService.changeRecordStatus());
    }
}
