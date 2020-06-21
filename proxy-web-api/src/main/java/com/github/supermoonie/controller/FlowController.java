package com.github.supermoonie.controller;

import com.github.supermoonie.bo.Flow;
import com.github.supermoonie.service.FlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author supermoonie
 * @date 2020-06-11
 */
@Api
@RestController
@RequestMapping(value = "/flow")
public class FlowController {

    @Resource
    private FlowService flowService;

    @ApiOperation(value = "根据条件过滤")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "host", value = "host", paramType = "query"),
            @ApiImplicitParam(name = "port", value = "port", paramType = "query"),
            @ApiImplicitParam(name = "contentType", value = "Content-Type", paramType = "query"),
            @ApiImplicitParam(name = "start", value = "start", dataTypeClass = Date.class, paramType = "query")
    })
    @GetMapping(value = "/fetch")
    @CrossOrigin
    public ResponseEntity<List<Flow>> fetch(String host, Integer port, String contentType,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start) {
        List<Flow> flows = flowService.fetch(host, port, contentType, start);
        return ResponseEntity.ok(flows);
    }
}
