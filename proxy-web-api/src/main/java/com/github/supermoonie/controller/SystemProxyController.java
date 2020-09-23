package com.github.supermoonie.controller;

import com.github.supermoonie.proxy.platform.ProxySetup;
import com.github.supermoonie.runner.InternalProxyRunner;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author supermoonie
 * @since 2020/9/23
 */
@Api
@RestController
@RequestMapping(value = "/system/proxy")
@CrossOrigin
@Slf4j
public class SystemProxyController {

    @Resource
    private InternalProxyRunner internalProxyRunner;

    @GetMapping("/status")
    public ResponseEntity<Boolean> status() {
        int port = internalProxyRunner.getProxy().getPort();
        try {
            boolean enable = ProxySetup.isEnable("127.0.0.1", port);
            return ResponseEntity.ok(enable);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/enable")
    public ResponseEntity<String> enable() {
        int port = internalProxyRunner.getProxy().getPort();
        String username = internalProxyRunner.getProxy().getUsername();
        String password = internalProxyRunner.getProxy().getPassword();
        try {
            ProxySetup.enableHttpProxy("127.0.0.1", port, username, password);
            return ResponseEntity.ok("success");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/disable")
    public ResponseEntity<String> disable() {
        try {
            ProxySetup.disableHttpProxy();
            return ResponseEntity.ok("success");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
