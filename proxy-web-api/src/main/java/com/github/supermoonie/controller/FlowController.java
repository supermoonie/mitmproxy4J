package com.github.supermoonie.controller;

import com.github.supermoonie.dto.FlowDTO;
import com.github.supermoonie.dto.FlowNode;
import com.github.supermoonie.dto.SimpleRequestDTO;
import com.github.supermoonie.service.FlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin
public class FlowController {

    @Resource
    private FlowService flowService;

    @ApiOperation(value = "根据条件获取list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "host", value = "host", paramType = "formData"),
            @ApiImplicitParam(name = "method", value = "method", paramType = "formData"),
            @ApiImplicitParam(name = "start", value = "start", dataTypeClass = Date.class, paramType = "formData"),
            @ApiImplicitParam(name = "end", value = "end", dataTypeClass = Date.class, paramType = "formData"),
    })
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<List<SimpleRequestDTO>> list(String host, String method,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end) {
        List<SimpleRequestDTO> list = flowService.list(host, method, start, end);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "根据条件获取tree", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "host", value = "host", paramType = "formData"),
            @ApiImplicitParam(name = "method", value = "method", paramType = "formData"),
            @ApiImplicitParam(name = "start", value = "start", dataTypeClass = Date.class, paramType = "formData"),
            @ApiImplicitParam(name = "end", value = "end", dataTypeClass = Date.class, paramType = "formData"),
    })
    @PostMapping(value = "/tree", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<List<FlowNode>> tree(String host, String method,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end) {
        List<FlowNode> simpleFlows = flowService.tree(host, method, start, end);
        return ResponseEntity.ok(simpleFlows);
    }

    @ApiOperation(value = "根据requestId获取flow")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", value = "requestId", paramType = "path"),
    })
    @GetMapping(value = "/detail/{requestId}")
    public ResponseEntity<FlowDTO> detail(@PathVariable("requestId") String requestId) {
        FlowDTO detail = flowService.detail(requestId);
        return ResponseEntity.ok(detail);
    }
}
