package com.github.supermoonie.proxy.intercept;


import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.util.RequestUtils;
import com.github.supermoonie.util.ResponseUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public class ConfigurableIntercept implements RequestIntercept, ResponseIntercept {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurableIntercept.class);

    private final List<String> blackUriList = new ArrayList<>();
    private final List<String> blackHostList = new ArrayList<>();
    private final List<String> whiteUriList = new ArrayList<>();
    private final List<String> whiteHostList = new ArrayList<>();
    private final List<String> useSecondProxyHostList = new ArrayList<>();
    private final List<String> notUseSecondProxyHostList = new ArrayList<>();
    private final Map<String, String> remoteUriMap = new HashMap<>();
    private Map<String, String> localMap;

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        String uri = request.uri();
        if (blackUriList.contains(uri)) {
            return ResponseUtils.htmlResponse("Uri Blocked!", HttpResponseStatus.OK);
        }
        if (whiteUriList.size() > 0 && !whiteUriList.contains(uri)) {
            return ResponseUtils.htmlResponse("Not In Uri White List!", HttpResponseStatus.OK);
        }
        String host = request.headers().get(HttpHeaderNames.HOST);
        if (blackHostList.contains(host)) {
            return ResponseUtils.htmlResponse("Host Blocked!", HttpResponseStatus.OK);
        }
        if (whiteHostList.size() > 0 && !whiteHostList.contains(host)) {
            return ResponseUtils.htmlResponse("Not In Host White List!", HttpResponseStatus.OK);
        }
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

    public List<String> getBlackUriList() {
        return blackUriList;
    }

    public List<String> getWhiteUriList() {
        return whiteUriList;
    }

    public List<String> getBlackHostList() {
        return blackHostList;
    }

    public List<String> getWhiteHostList() {
        return whiteHostList;
    }

    public Map<String, String> getRemoteUriMap() {
        return remoteUriMap;
    }

    public List<String> getUseSecondProxyHostList() {
        return useSecondProxyHostList;
    }

    public List<String> getNotUseSecondProxyHostList() {
        return notUseSecondProxyHostList;
    }
}
