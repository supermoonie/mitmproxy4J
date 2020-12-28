package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.InterceptContext;
import com.github.supermoonie.proxy.util.RequestUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/12/28
 */
public class RemoteMapIntercept implements RequestIntercept, ResponseIntercept {

    private boolean remoteMapFlag = false;
    private final Map<String, String> remoteUriMap = new HashMap<>();

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, HttpRequest request) {
        String uri = ctx.getConnectionInfo().getUrl();
        if (remoteMapFlag) {
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
        }
        return null;
    }

    @Override
    public FullHttpResponse onResponse(InterceptContext ctx, HttpRequest request, FullHttpResponse response) {
        return null;
    }

    public boolean isRemoteMapFlag() {
        return remoteMapFlag;
    }

    public void setRemoteMapFlag(boolean remoteMapFlag) {
        this.remoteMapFlag = remoteMapFlag;
    }

    public Map<String, String> getRemoteUriMap() {
        return remoteUriMap;
    }
}
