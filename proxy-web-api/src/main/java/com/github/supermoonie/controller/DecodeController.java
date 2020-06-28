package com.github.supermoonie.controller;

import cn.hutool.core.util.HexUtil;
import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.supermoonie.util.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/6/26
 */
@Api(value = "解码", tags = "format")
@RestController
@RequestMapping("/decode")
@Slf4j
public class DecodeController {

    @CrossOrigin
    @ApiOperation(value = "Decode Hex to String", tags = "decode")
    @PostMapping(value = "/hex")
    public ResponseEntity<String> hex(@RequestParam("hex") String hex) {
        try {
            return ResponseEntity.ok(HexUtil.decodeHexStr(hex));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.HTTP_BAD_REQUEST).body("Broken Hex, Error: " + e.getMessage());
        }
    }

    @CrossOrigin
    @ApiOperation(value = "Hex to JSON", tags = "decode")
    @PostMapping(value = "/hex/to/json")
    public ResponseEntity<String> hexToJson(@RequestParam("hex") String hex) {
        try {
            Map<String, Object> map = JSON.parse(HexUtil.decodeHexStr(hex), new TypeReference<Map<String, Object>>() {
            });
            String json = JSON.toJsonString(map, true);
            return ResponseEntity.ok(json);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.HTTP_BAD_REQUEST).body("Broken JSON, Error: " + e.getMessage());
        }
    }
}
