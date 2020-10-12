package com.github.supermoonie.proxy.fx.proxy.intercept;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.fx.config.GlobalSetting;
import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import com.github.supermoonie.proxy.fx.constant.EnumYesNo;
import com.github.supermoonie.proxy.fx.dto.FlowNode;
import com.github.supermoonie.proxy.fx.entity.Config;
import com.github.supermoonie.proxy.fx.entity.Request;
import com.github.supermoonie.proxy.fx.entity.Response;
import com.github.supermoonie.proxy.fx.mapper.ConfigMapper;
import com.github.supermoonie.proxy.fx.service.ConfigService;
import com.github.supermoonie.proxy.fx.service.ResponseService;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author supermoonie
 * @since 2020/9/11
 */
@Component
public class DumpHttpResponseIntercept implements ResponseIntercept {

    private final Logger log = LoggerFactory.getLogger(DumpHttpResponseIntercept.class);

    @Resource
    private ResponseService responseService;

    @Resource
    private ConfigMapper configMapper;

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {

        if (null == ctx.getUserData()) {
            return null;
        }
        if (GlobalSetting.isRecord) {
            Request req = (Request) ctx.getUserData();
            Response res = responseService.saveResponse(response, req);
            log.info("response saved, uri: {}", request.uri());
            List<FlowNode> flow = generateFlow(req, res);

//            SimpleRequestDTO simpleRequest = new SimpleRequestDTO();
//            simpleRequest.setId(req.getId());
//            simpleRequest.setStatus(res.getStatus());
//            simpleRequest.setUrl(req.getUri());
//            asyncService.execute(() -> messagingTemplate.sendJson(simpleRequest));
        }
        return null;
    }

    private List<FlowNode> generateFlow(Request request, Response response) {
        List<FlowNode> flow = new ArrayList<>();
        String uri = request.getUri();
        if (StringUtils.isEmpty(uri)) {
            return null;
        }
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        FlowNode baseFlow = getBaseFlow(flow, url);
        if (StringUtils.isEmpty(url.getPath()) || "/".equals(url.getPath())) {
            setRootPathFlowNode(baseFlow, request, response);
        } else {
            setPathFlowNode(baseFlow, request, response, url);
        }
        return flow;
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
            flow.setType(EnumFlowType.BASE_URL);
            tree.add(flow);
            return flow;
        });
    }

    /**
     * 设置根路径的节点
     *
     * @param baseFlow 根结点
     * @param request  {@link Request}
     * @param response {@link Response}
     */
    private void setRootPathFlowNode(FlowNode baseFlow, Request request, Response response) {
        FlowNode child = new FlowNode();
        child.setId(request.getId());
        child.setUrl("/");
        child.setType(EnumFlowType.TARGET);
        child.setStatus(response.getStatus());
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
     * @param request  {@link Request}
     * @param response {@link Response}
     * @param url      url
     */
    private void setPathFlowNode(FlowNode baseFlow, Request request, Response response, URL url) {
        String path = url.getPath().substring(1) + " ";
        String[] parts = path.split("/");
        int len = parts.length;
        FlowNode currentParent = baseFlow;
        for (int i = 0; i < len; i++) {
            String part = parts[i].equals(" ") ? "/" : parts[i];
            List<FlowNode> children = currentParent.getChildren();
            if (null == children) {
                children = new ArrayList<>();
                currentParent.setChildren(children);
            }
            if (i == (len - 1)) {
                addLastPathFlowNode(children, request, response, part);
                break;
            }
            FlowNode child;
            Optional<FlowNode> first = children.stream().filter(flow -> flow.getUrl().equals(part)).findFirst();
            if (first.isEmpty()) {
                child = new FlowNode();
                child.setUrl(part);
                child.setId(part);
                child.setType(EnumFlowType.PATH);
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
     * @param request  {@link Request}
     * @param response {@link Response}
     * @param part     最后一个path
     */
    private void addLastPathFlowNode(List<FlowNode> children, Request request, Response response, String part) {
        FlowNode child = new FlowNode();
        child.setId(request.getId());
        child.setStatus(response.getStatus());
        child.setType(EnumFlowType.TARGET);
        child.setUrl(part);
        children.add(child);
    }
}
