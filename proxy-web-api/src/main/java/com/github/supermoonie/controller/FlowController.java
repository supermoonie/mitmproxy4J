package com.github.supermoonie.controller;

import com.github.supermoonie.dto.FlowDTO;
import com.github.supermoonie.dto.FlowNode;
import com.github.supermoonie.dto.SimpleRequestDTO;
import com.github.supermoonie.service.FlowService;
import com.github.supermoonie.util.JSON;
import com.github.supermoonie.ws.MessagingTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
@Slf4j
public class FlowController {

    @Resource
    private FlowService flowService;

    @Resource
    private MessagingTemplate messagingTemplate;

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

    @ApiOperation(value = "根据requestId下载flow")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", value = "requestId", paramType = "path"),
    })
    @GetMapping(value = "/save/{requestId}")
    public void save(@PathVariable("requestId") String requestId, HttpServletResponse response) {
        String detail = JSON.toJsonString(flowService.detail(requestId), true);
        response.addHeader("Content-Disposition", "attachment; fileName=" + requestId + ".json");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        try {
            IOUtils.write(detail, response.getOutputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "根据requestId下载flow")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", value = "requestId", paramType = "path"),
    })
    @PostMapping(value = "/read")
    public ResponseEntity<SimpleRequestDTO> read(@RequestParam("flow") MultipartFile flow) {
        try {
            byte[] bytes = IOUtils.readFully(flow.getInputStream(), (int) flow.getSize());
            FlowDTO flowDTO = JSON.parse(new String(bytes, StandardCharsets.UTF_8), FlowDTO.class);
            SimpleRequestDTO simpleRequest = flowService.save(flowDTO);
            messagingTemplate.sendJson(simpleRequest);
            return ResponseEntity.ok(simpleRequest);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
