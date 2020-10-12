package com.github.supermoonie.proxy.fx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.fx.mapper.ContentMapper;
import com.github.supermoonie.proxy.fx.mapper.HeaderMapper;
import com.github.supermoonie.proxy.fx.mapper.RequestMapper;
import com.github.supermoonie.proxy.fx.mapper.ResponseMapper;
import com.github.supermoonie.proxy.fx.service.FlowService;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author supermoonie
 * @date 2020-06-11
 */
@Service
public class FlowServiceImpl implements FlowService {

    @Resource
    private RequestMapper requestMapper;

    @Resource
    private HeaderMapper headerMapper;

    @Resource
    private ResponseMapper responseMapper;

    @Resource
    private ContentMapper contentMapper;

//    @Override
//    public List<SimpleRequestDTO> list(String host, String method, Date start, Date end) {
//        host = StringUtils.isEmpty(host) ? null : host;
//        method = StringUtils.isEmpty(method) ? null : "all".equalsIgnoreCase(method) ? null : method;
//        String startTime = null != start ? DateUtil.format(start, "yyyy-MM-dd HH:mm:ss") : null;
//        String endTime = null != end ? DateUtil.format(end, "yyyy-MM-dd HH:mm:ss") : null;
//        List<SimpleRequestDAO> requestList = requestMapper.selectSimple(host, method, startTime, endTime);
//        return requestList.stream().map(request -> {
//            SimpleRequestDTO dto = new SimpleRequestDTO();
//            dto.setId(request.getId());
//            dto.setUrl(request.getUri());
//            dto.setStatus(request.getStatus());
//            return dto;
//        }).collect(Collectors.toList());
//    }

//    @Override
//    public List<FlowNode> tree(String host, String method, Date start, Date end) {
//        List<FlowNode> tree = new ArrayList<>();
//        host = StringUtils.isEmpty(host) ? null : host;
//        method = StringUtils.isEmpty(method) ? null : "all".equalsIgnoreCase(method) ? null : method;
//        String startTime = null != start ? DateUtil.format(start, "yyyy-MM-dd HH:mm:ss") : null;
//        String endTime = null != end ? DateUtil.format(end, "yyyy-MM-dd HH:mm:ss") : null;
//        List<SimpleRequestDAO> simpleRequestList = requestMapper.selectSimple(host, method, startTime, endTime);
//        for (SimpleRequestDAO request : simpleRequestList) {
//            String uri = request.getUri();
//            if (StringUtils.isEmpty(uri)) {
//                continue;
//            }
//            URL url;
//            try {
//                url = new URL(uri);
//            } catch (MalformedURLException e) {
//                log.error(e.getMessage(), e);
//                continue;
//            }
//            FlowNode baseFlow = getBaseFlow(tree, url);
//            if (StringUtils.isEmpty(url.getPath()) || "/".equals(url.getPath())) {
//                setRootPathFlowNode(baseFlow, request);
//            } else {
//                setPathFlowNode(baseFlow, request, url);
//            }
//        }
//        return tree;
//    }
//
//    /**
//     * 根据url获取根结点
//     *
//     * @param tree 树状链接结构
//     * @param url  url
//     * @return 根结点，如果有就返回，没有则创建
//     */
//    private FlowNode getBaseFlow(List<FlowNode> tree, URL url) {
//        String baseUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort());
//        return tree.stream().filter(flow -> flow.getUrl().equals(baseUrl)).findFirst().orElseGet(() -> {
//            FlowNode flow = new FlowNode();
//            flow.setId("0");
//            flow.setUrl(baseUrl);
//            flow.setType(EnumFlowNodeType.BASE_URL);
//            tree.add(flow);
//            return flow;
//        });
//    }
//
//    /**
//     * 设置根路径的节点
//     *
//     * @param baseFlow 根结点
//     * @param request  {@link SimpleRequestDAO}
//     */
//    private void setRootPathFlowNode(FlowNode baseFlow, SimpleRequestDAO request) {
//        FlowNode child = new FlowNode();
//        child.setId(request.getId());
//        child.setUrl("/");
//        child.setType(EnumFlowNodeType.TARGET);
//        child.setStatus(request.getStatus());
//        List<FlowNode> children = baseFlow.getChildren();
//        if (null == children) {
//            children = new ArrayList<>();
//        }
//        children.add(child);
//        baseFlow.setChildren(children);
//    }
//
//    /**
//     * 解析url的路径信息并设置路径节点
//     *
//     * @param baseFlow 根结点
//     * @param request  {@link SimpleRequestDAO}
//     * @param url      url
//     */
//    private void setPathFlowNode(FlowNode baseFlow, SimpleRequestDAO request, URL url) {
//        String path = url.getPath().substring(1) + " ";
//        String[] parts = path.split("/");
//        int len = parts.length;
//        FlowNode currentParent = baseFlow;
//        for (int i = 0; i < len; i++) {
//            String part = parts[i].equals(" ") ? "/" : parts[i];
//            List<FlowNode> children = currentParent.getChildren();
//            if (null == children) {
//                children = new ArrayList<>();
//                currentParent.setChildren(children);
//            }
//            if (i == (len - 1)) {
//                addLastPathFlowNode(children, request, part);
//                break;
//            }
//            FlowNode child;
//            Optional<FlowNode> first = children.stream().filter(flow -> flow.getUrl().equals(part)).findFirst();
//            if (first.isEmpty()) {
//                child = new FlowNode();
//                child.setUrl(part);
//                child.setId(part);
//                child.setType(EnumFlowNodeType.PATH);
//                children.add(child);
//            } else {
//                child = first.get();
//            }
//            currentParent = child;
//        }
//    }
//
//    /**
//     * 增加最后一个path节点
//     *
//     * @param children 子节点集合
//     * @param request  {@link SimpleRequestDAO}
//     * @param part     最后一个path
//     */
//    private void addLastPathFlowNode(List<FlowNode> children, SimpleRequestDAO request, String part) {
//        FlowNode child = new FlowNode();
//        child.setId(request.getId());
//        child.setStatus(request.getStatus());
//        child.setType(EnumFlowNodeType.TARGET);
//        child.setUrl(part);
//        children.add(child);
//    }
//
//    @Override
//    public FlowDTO detail(String requestId) {
//        if (StringUtils.isEmpty(requestId)) {
//            return null;
//        }
//        Request request = requestMapper.selectById(requestId);
//        if (null == request) {
//            return null;
//        }
//        return from(request);
//    }
//
//    @Transactional(rollbackFor = RuntimeException.class)
//    @Override
//    public SimpleRequestDTO save(FlowDTO flow) {
//        RequestDTO request = flow.getRequest();
//        Request req = new Request();
//        BeanUtils.copyProperties(request, req);
//        String requestId = UUID.randomUUID().toString();
//        req.setId(requestId);
//        requestMapper.insert(req);
//        flow.getRequestHeaders().forEach(h -> {
//            Header header = new Header();
//            header.setId(UUID.randomUUID().toString());
//            header.setName(h.getName());
//            header.setValue(h.getValue());
//            header.setRequestId(requestId);
//            header.setTimeCreated(new Date());
//            headerMapper.insert(header);
//        });
//        String requestContent = flow.getRequestContent();
//        if (!StringUtils.isEmpty(requestContent)) {
//            Content reqContent = new Content();
//            reqContent.setContent(Hex.decode(requestContent));
//            reqContent.setId(UUID.randomUUID().toString());
//            reqContent.setTimeCreated(new Date());
//            contentMapper.insert(reqContent);
//        }
//        ResponseDTO response = flow.getResponse();
//        Response res = new Response();
//        BeanUtils.copyProperties(response, res);
//        String responseId = UUID.randomUUID().toString();
//        res.setId(responseId);
//        res.setRequestId(requestId);
//        responseMapper.insert(res);
//        int status = -1;
//        for (int i = 0; i < flow.getRequestHeaders().size(); i++) {
//            HeaderDTO h = flow.getRequestHeaders().get(i);
//            if ("status".equalsIgnoreCase(h.getName())) {
//                status = Integer.parseInt(h.getValue());
//            }
//            Header header = new Header();
//            header.setId(UUID.randomUUID().toString());
//            header.setName(h.getName());
//            header.setValue(h.getValue());
//            header.setResponseId(responseId);
//            header.setTimeCreated(new Date());
//            headerMapper.insert(header);
//        }
//        String responseContent = flow.getResponseContent();
//        if (!StringUtils.isEmpty(responseContent)) {
//            Content resContent = new Content();
//            resContent.setContent(Hex.decode(responseContent));
//            resContent.setId(UUID.randomUUID().toString());
//            resContent.setTimeCreated(new Date());
//            contentMapper.insert(resContent);
//        }
//        SimpleRequestDTO simpleRequest = new SimpleRequestDTO();
//        simpleRequest.setId(requestId);
//        simpleRequest.setStatus(status);
//        simpleRequest.setUrl(request.getUri());
//        return simpleRequest;
//    }
//
//    private FlowDTO from(Request request) {
//        FlowDTO flow = new FlowDTO();
//        // request
//        RequestDTO requestDTO = new RequestDTO();
//        BeanUtils.copyProperties(request, requestDTO);
//        requestDTO.setTimeCreated(request.getTimeCreated().getTime());
//        flow.setRequest(requestDTO);
//        // request headers
//        setFlowRequestHeaders(flow, request.getId());
//        // request content
//        setFlowRequestContent(flow, request.getContentId());
//        // response
//        setFlowResponse(flow, request.getId());
//        return flow;
//    }
//
//    private void setFlowRequestHeaders(FlowDTO flow, String requestId) {
//        List<Header> requestHeaderList = headerMapper.selectList(new QueryWrapper<Header>()
//                .eq("requestId", requestId).select("name", "value"));
//        flow.setRequestHeaders(requestHeaderList.stream().map(header -> {
//            HeaderDTO headerDTO = new HeaderDTO();
//            headerDTO.setName(header.getName());
//            headerDTO.setValue(header.getValue());
//            return headerDTO;
//        }).collect(Collectors.toList()));
//    }
//
//    private void setFlowRequestContent(FlowDTO flow, String requestContentId) {
//        if (!StringUtils.isEmpty(requestContentId)) {
//            Content content = contentMapper.selectOne(new QueryWrapper<Content>().eq("id", requestContentId));
//            if (null != content) {
//                flow.setRequestContent(HexUtil.encodeHexStr(content.getContent()));
//            }
//        }
//    }
//
//    private void setFlowResponse(FlowDTO flow, String requestId) {
//        Response response = responseMapper.selectOne(new QueryWrapper<Response>()
//                .eq("requestId", requestId).select("id", "contentId", "timeCreated", "httpVersion", "status", "contentType"));
//        if (null != response) {
//            ResponseDTO responseDTO = new ResponseDTO();
//            BeanUtils.copyProperties(response, responseDTO);
//            responseDTO.setTimeCreated(response.getTimeCreated().getTime());
//            flow.setResponse(responseDTO);
//            // response headers
//            setFlowResponseHeaders(flow, response.getId());
//            // response content
//            setFlowResponseContent(flow, response.getContentId());
//        }
//    }
//
//    private void setFlowResponseHeaders(FlowDTO flow, String responseId) {
//        List<Header> responseHeaderList = headerMapper.selectList(new QueryWrapper<Header>()
//                .eq("responseId", responseId).select("name", "value"));
//        flow.setResponseHeaders(responseHeaderList.stream().map(header -> {
//            HeaderDTO headerDTO = new HeaderDTO();
//            headerDTO.setName(header.getName());
//            headerDTO.setValue(header.getValue());
//            return headerDTO;
//        }).collect(Collectors.toList()));
//    }
//
//    private void setFlowResponseContent(FlowDTO flow, String responseContentId) {
//        Content content = contentMapper.selectOne(new QueryWrapper<Content>()
//                .eq("id", responseContentId).select("content"));
//        if (null != content) {
//            flow.setResponseContent(HexUtil.encodeHexStr(content.getContent()));
//        }
//    }
}
