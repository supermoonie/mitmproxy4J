package com.github.supermoonie.proxy.intercept;


import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.util.RequestUtils;
import com.github.supermoonie.proxy.util.ResponseUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public class ConfigurableIntercept implements RequestIntercept, ResponseIntercept {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurableIntercept.class);

    private boolean black = false;
    private final Set<String> blockUriList = new HashSet<>();
    private boolean white = false;
    private final Set<String> allowUriList = new HashSet<>();
    private final List<String> useSecondProxyHostList = new ArrayList<>();
    private final List<String> notUseSecondProxyHostList = new ArrayList<>();
    private final Map<String, String> remoteUriMap = new HashMap<>();
    private Map<String, String> localMap;

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        String uri = request.uri();
        if (black) {
            for (String reg : blockUriList) {
                if (uri.matches(reg)) {
                    return ResponseUtils.htmlResponse("Uri Blocked!", HttpResponseStatus.OK);
                }
            }
        }
        if (white) {
            for (String reg : allowUriList) {
                if (!uri.matches(reg)) {
                    return ResponseUtils.htmlResponse("Not In Uri White List!", HttpResponseStatus.OK);
                }
            }
        }
        String host = request.headers().get(HttpHeaderNames.HOST);
        String remoteUri = remoteUriMap.get(uri);
        ConnectionInfo info = RequestUtils.parseUri(remoteUri);
        if (null != remoteUri && null != info) {
            request.setUri(remoteUri);
            request.headers().set(HttpHeaderNames.HOST, info.getRemoteHost());
            ConnectionInfo originInfo = ctx.getConnectionInfo();
            originInfo.setRemoteHost(info.getRemoteHost());
            originInfo.setRemotePort(info.getRemotePort());
            originInfo.setHostHeader(info.getRemoteHost() + ":" + info.getRemotePort());
        }
        if (useSecondProxyHostList.size() > 0) {
            ctx.getConnectionInfo().setUseSecondProxy(useSecondProxyHostList.contains(host));
        }
        if (notUseSecondProxyHostList.size() > 0) {
            ctx.getConnectionInfo().setUseSecondProxy(!notUseSecondProxyHostList.contains(host));
        }
        return null;
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        return null;
    }

    public Set<String> getAllowUriList() {
        return allowUriList;
    }

    public Map<String, String> getRemoteUriMap() {
        return remoteUriMap;
    }

    public List<String> getUseSecondProxyHostList() {
        return useSecondProxyHostList;
    }

    public Set<String> getBlockUriList() {
        return blockUriList;
    }

    public List<String> getNotUseSecondProxyHostList() {
        return notUseSecondProxyHostList;
    }

    public boolean isBlack() {
        return black;
    }

    public void setBlack(boolean black) {
        this.black = black;
    }
}
