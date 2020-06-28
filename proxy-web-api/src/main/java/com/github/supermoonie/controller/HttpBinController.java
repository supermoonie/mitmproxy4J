package com.github.supermoonie.controller;

import com.github.supermoonie.controller.vo.HttpRequestVO;
import com.github.supermoonie.util.RequestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author supermoonie
 * @since 2020/6/25
 */
@Api(value = "https://httpbin.org", tags = "http")
@RestController
@RequestMapping(value = "")
public class HttpBinController {

    @ApiOperation(value = "GET", tags = "http", httpMethod = "GET")
    @GetMapping(value = "/get")
    public ResponseEntity<HttpRequestVO> get(HttpServletRequest request) {
        HttpRequestVO vo = RequestUtils.format(request);
        return ResponseEntity.ok(vo);
    }

    @ApiOperation(value = "POST", tags = "http", httpMethod = "POST")
    @PostMapping(value = "/post")
    public ResponseEntity<HttpRequestVO> post(HttpServletRequest request) {
        HttpRequestVO vo = RequestUtils.format(request);
        return ResponseEntity.ok(vo);
    }




}
