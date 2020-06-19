package com.github.supermoonie.service.impl;

import cn.hutool.core.util.HexUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.bo.Flow;
import com.github.supermoonie.bo.HeaderBO;
import com.github.supermoonie.mapper.ContentMapper;
import com.github.supermoonie.mapper.HeaderMapper;
import com.github.supermoonie.mapper.RequestMapper;
import com.github.supermoonie.mapper.ResponseMapper;
import com.github.supermoonie.model.Content;
import com.github.supermoonie.model.Header;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import com.github.supermoonie.service.FlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @date 2020-06-11
 */
@Service
@Slf4j
public class FlowServiceImpl implements FlowService {

    @Resource
    private RequestMapper requestMapper;

    @Resource
    private HeaderMapper headerMapper;

    @Resource
    private ResponseMapper responseMapper;

    @Resource
    private ContentMapper contentMapper;

    @Override
    public List<Flow> fetch(String host, Integer port, String contentType, Date start) {
        List<Flow> result = new ArrayList<>();
        QueryWrapper<Request> requestQueryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(host)) {
            requestQueryWrapper.like("host", host.trim());
        }
        if (null != port && port > 0) {
            requestQueryWrapper.eq("port", port);
        }
        requestQueryWrapper.ge("timeCreated", start);
        requestQueryWrapper.isNotNull("host");
        requestQueryWrapper.orderByDesc("timeCreated");
        List<Request> requests = requestMapper.selectList(requestQueryWrapper);
        requests.forEach(request -> {
            Flow flow = new Flow();
            flow.setRequest(request);
            List<Header> requestHeaderList = headerMapper.selectList(new QueryWrapper<Header>()
                    .eq("requestId", request.getId()).select("name", "value"));
            flow.setRequestHeaders(requestHeaderList.stream().map(header -> {
                HeaderBO headerBO = new HeaderBO();
                headerBO.setName(header.getName());
                headerBO.setValue(header.getValue());
                return headerBO;
            }).collect(Collectors.toList()));
            if (!StringUtils.isEmpty(request.getContentId())) {
                Content content = contentMapper.selectOne(new QueryWrapper<Content>().eq("id", request.getContentId()));
                if (null != content) {
                    flow.setRequestContent(HexUtil.encodeHexStr(content.getContent()));
                }
            }
            Response response = responseMapper.selectOne(new QueryWrapper<Response>()
                    .eq("requestId", request.getId()).select("id", "contentId", "timeCreated", "httpVersion", "status", "contentType"));
            if (null != response) {
                if (!StringUtils.isEmpty(contentType)) {
                    if (response.getContentType().contains(contentType)) {
                        fillResponse(response, flow);
                        result.add(flow);
                    }
                } else {
                    fillResponse(response, flow);
                    result.add(flow);
                }
            } else {
                if (StringUtils.isEmpty(contentType)) {
                    result.add(flow);
                }
            }
        });
        return result;
    }

    private void fillResponse(Response response, Flow flow) {
        flow.setResponse(response);
        List<Header> responseHeaderList = headerMapper.selectList(new QueryWrapper<Header>()
                .eq("responseId", response.getId()).select("name", "value"));
        flow.setResponseHeaders(responseHeaderList.stream().map(header -> {
            HeaderBO headerBO = new HeaderBO();
            headerBO.setName(header.getName());
            headerBO.setValue(header.getValue());
            return headerBO;
        }).collect(Collectors.toList()));
        Content content = contentMapper.selectOne(new QueryWrapper<Content>()
                .eq("id", response.getContentId()).select("content"));
        if (null != content) {
            flow.setResponseContent(HexUtil.encodeHexStr(content.getContent()));
        }
    }
}
