package com.github.supermoonie.service.impl;

import cn.hutool.core.util.HexUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.constant.EnumFlowNodeType;
import com.github.supermoonie.dto.*;
import com.github.supermoonie.mapper.ContentMapper;
import com.github.supermoonie.mapper.HeaderMapper;
import com.github.supermoonie.mapper.RequestMapper;
import com.github.supermoonie.mapper.ResponseMapper;
import com.github.supermoonie.mapper.dao.SimpleRequestDAO;
import com.github.supermoonie.model.Content;
import com.github.supermoonie.model.Header;
import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import com.github.supermoonie.service.FlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    public List<SimpleRequestDTO> list(String host, String method, Date start, Date end) {
        if (StringUtils.isEmpty(host)) {
            host = null;
        }
        if (StringUtils.isEmpty(method)) {
            method = null;
        }
        List<SimpleRequestDAO> requestList = requestMapper.selectSimple(host, method, start, end);
        return requestList.stream().map(request -> {
            SimpleRequestDTO dto = new SimpleRequestDTO();
            dto.setId(request.getId());
            dto.setUrl(request.getUri());
            dto.setStatus(request.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FlowNode> tree(String host, String method, Date start, Date end) {
        List<FlowNode> tree = new ArrayList<>();
        if (StringUtils.isEmpty(host)) {
            host = null;
        }
        if (StringUtils.isEmpty(method)) {
            method = null;
        }
        List<SimpleRequestDAO> simpleRequestList = requestMapper.selectSimple(host, method, start, end);
        for (SimpleRequestDAO request : simpleRequestList) {
            String uri = request.getUri();
            if (StringUtils.isEmpty(uri)) {
                continue;
            }
            URL url;
            try {
                url = new URL(uri);
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
                continue;
            }
            FlowNode baseFlow = getBaseFlow(tree, url);
            if (StringUtils.isEmpty(url.getPath()) || "/".equals(url.getPath())) {
                setRootPathFlowNode(baseFlow, request);
            } else {
                setPathFlowNode(baseFlow, request, url);
            }
        }
        return tree;
    }

    /**
     * 根据url获取根结点
     *
     * @param tree 树状链接结构
     * @param url  url
     * @return 根结点，如果有就返回，没有则创建
     */
    private FlowNode getBaseFlow(List<FlowNode> tree, URL url) {
        String baseUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
        return tree.stream().filter(flow -> flow.getUrl().equals(baseUrl)).findFirst().orElseGet(() -> {
            FlowNode flow = new FlowNode();
            flow.setId("0");
            flow.setUrl(baseUrl);
            flow.setType(EnumFlowNodeType.BASE_URL);
            tree.add(flow);
            return flow;
        });
    }

    /**
     * 设置根路径的节点
     *
     * @param baseFlow 根结点
     * @param request  {@link SimpleRequestDAO}
     */
    private void setRootPathFlowNode(FlowNode baseFlow, SimpleRequestDAO request) {
        FlowNode child = new FlowNode();
        child.setId(request.getId());
        child.setUrl("/");
        child.setType(EnumFlowNodeType.TARGET);
        child.setStatus(request.getStatus());
        List<FlowNode> children = baseFlow.getChildren();
        if (null == children) {
            children = new ArrayList<>();
        }
        children.add(child);
        baseFlow.setChildren(children);
    }

    /**
     * 解析url的路径信息并设置路径节点
     *
     * @param baseFlow 根结点
     * @param request  {@link SimpleRequestDAO}
     * @param url      url
     */
    private void setPathFlowNode(FlowNode baseFlow, SimpleRequestDAO request, URL url) {
        String path = url.getPath().substring(1);
        String[] parts = path.split("/");
        int len = parts.length;
        FlowNode currentParent = baseFlow;
        for (int i = 0; i < len; i++) {
            String part = parts[i];
            List<FlowNode> children = currentParent.getChildren();
            if (null == children) {
                children = new ArrayList<>();
                currentParent.setChildren(children);
            }
            FlowNode child;
            if (i == (len - 1)) {
                addLastPathFlowNode(children, request, part);
                break;
            }
            Optional<FlowNode> first = children.stream().filter(flow -> flow.getUrl().equals(part)).findFirst();
            if (!first.isPresent()) {
                child = new FlowNode();
                child.setUrl(part);
                child.setId(part);
                child.setType(EnumFlowNodeType.PATH);
                children.add(child);
            } else {
                child = first.get();
            }
            currentParent = child;
        }
    }

    /**
     * 增加最后一个path节点
     *
     * @param children 子节点集合
     * @param request  {@link SimpleRequestDAO}
     * @param part     最后一个path
     */
    private void addLastPathFlowNode(List<FlowNode> children, SimpleRequestDAO request, String part) {
        FlowNode child = new FlowNode();
        child.setId(request.getId());
        child.setStatus(request.getStatus());
        child.setType(EnumFlowNodeType.TARGET);
        child.setUrl(part);
        children.add(child);
    }

    @Override
    public FlowDTO detail(String requestId) {
        if (StringUtils.isEmpty(requestId)) {
            return null;
        }
        Request request = requestMapper.selectById(requestId);
        if (null == request) {
            return null;
        }
        return from(request);
    }

    private FlowDTO from(Request request) {
        FlowDTO flow = new FlowDTO();
        // request
        RequestDTO requestDTO = new RequestDTO();
        BeanUtils.copyProperties(request, requestDTO);
        requestDTO.setTimeCreated(request.getTimeCreated().getTime());
        flow.setRequest(requestDTO);
        // request headers
        setFlowRequestHeaders(flow, request.getId());
        // request content
        setFlowRequestContent(flow, request.getContentId());
        // response
        setFlowResponse(flow, request.getId());
        return flow;
    }

    private void setFlowRequestHeaders(FlowDTO flow, String requestId) {
        List<Header> requestHeaderList = headerMapper.selectList(new QueryWrapper<Header>()
                .eq("requestId", requestId).select("name", "value"));
        flow.setRequestHeaders(requestHeaderList.stream().map(header -> {
            HeaderDTO headerDTO = new HeaderDTO();
            headerDTO.setName(header.getName());
            headerDTO.setValue(header.getValue());
            return headerDTO;
        }).collect(Collectors.toList()));
    }

    private void setFlowRequestContent(FlowDTO flow, String requestContentId) {
        if (!StringUtils.isEmpty(requestContentId)) {
            Content content = contentMapper.selectOne(new QueryWrapper<Content>().eq("id", requestContentId));
            if (null != content) {
                flow.setRequestContent(HexUtil.encodeHexStr(content.getContent()));
            }
        }
    }

    private void setFlowResponse(FlowDTO flow, String requestId) {
        Response response = responseMapper.selectOne(new QueryWrapper<Response>()
                .eq("requestId", requestId).select("id", "contentId", "timeCreated", "httpVersion", "status", "contentType"));
        if (null != response) {
            ResponseDTO responseDTO = new ResponseDTO();
            BeanUtils.copyProperties(response, responseDTO);
            responseDTO.setTimeCreated(response.getTimeCreated().getTime());
            flow.setResponse(responseDTO);
            // response headers
            setFlowResponseHeaders(flow, response.getId());
            // response content
            setFlowResponseContent(flow, response.getContentId());
        }
    }

    private void setFlowResponseHeaders(FlowDTO flow, String responseId) {
        List<Header> responseHeaderList = headerMapper.selectList(new QueryWrapper<Header>()
                .eq("responseId", responseId).select("name", "value"));
        flow.setResponseHeaders(responseHeaderList.stream().map(header -> {
            HeaderDTO headerDTO = new HeaderDTO();
            headerDTO.setName(header.getName());
            headerDTO.setValue(header.getValue());
            return headerDTO;
        }).collect(Collectors.toList()));
    }

    private void setFlowResponseContent(FlowDTO flow, String responseContentId) {
        Content content = contentMapper.selectOne(new QueryWrapper<Content>()
                .eq("id", responseContentId).select("content"));
        if (null != content) {
            flow.setResponseContent(HexUtil.encodeHexStr(content.getContent()));
        }
    }
}
