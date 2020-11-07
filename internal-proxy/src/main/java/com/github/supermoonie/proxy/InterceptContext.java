package com.github.supermoonie.proxy;

import com.github.supermoonie.proxy.intercept.RequestIntercept;
import com.github.supermoonie.proxy.intercept.ResponseIntercept;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author supermoonie
 * @since 2020/8/12
 */
public class InterceptContext {

    private static final Logger logger = LoggerFactory.getLogger(InterceptContext.class);

    private ChannelHandlerContext nettyClientContext;

    private ChannelHandlerContext nettyRemoteContext;

    private Channel clientChannel;

    private Channel remoteChannel;

    private ConnectionInfo connectionInfo;

    private HttpRequest request;

    private FullHttpResponse fullHttpResponse;

    private Object userData;

    private final Map<String, RequestIntercept> requestIntercepts = new LinkedHashMap<>();

    private final Map<String, ResponseIntercept> responseIntercepts = new LinkedHashMap<>();

    void beforeRequest(HttpRequest request) {

    }

    boolean onRequest(HttpRequest request) {
        Set<String> keySet = requestIntercepts.keySet();
        for (String key : keySet) {
            RequestIntercept requestIntercept = requestIntercepts.get(key);
            FullHttpResponse response = requestIntercept.onRequest(this, request);
            logger.debug("requestIntercept: {}, response: {}", key, response);
            if (null != response) {
                response = onResponse(request, response);
                clientChannel.writeAndFlush(response);
                return false;
            }
        }
        return true;
    }

    FullHttpResponse onResponse(HttpRequest request, FullHttpResponse response) {
        Set<String> keySet = responseIntercepts.keySet();
        for (String key : keySet) {
            ResponseIntercept responseIntercept = responseIntercepts.get(key);
            FullHttpResponse httpResponse = responseIntercept.onResponse(this, request, response);
            logger.debug("responseIntercept: {}, response: {}", key, httpResponse);
            if (null != httpResponse) {
                return httpResponse;
            }
        }
        return response;
    }

    FullHttpResponse onRequestException(HttpRequest request, Throwable cause) {
        Set<String> keySet = requestIntercepts.keySet();
        for (String key : keySet) {
            RequestIntercept requestIntercept = requestIntercepts.get(key);
            FullHttpResponse response = requestIntercept.onException(this, request, cause);
            logger.debug("requestIntercept: {}, response: {}", key, response);
            if (null != response) {
                response = onResponse(request, response);
                clientChannel.writeAndFlush(response);
                return response;
            }
        }
        return null;
    }

    FullHttpResponse onResponseException(HttpRequest request, FullHttpResponse response, Throwable cause) {
        Set<String> keySet = responseIntercepts.keySet();
        for (String key : keySet) {
            ResponseIntercept responseIntercept = responseIntercepts.get(key);
            FullHttpResponse httpResponse = responseIntercept.onException(this, request, response, cause);
            logger.debug("responseIntercept: {}, response: {}", key, httpResponse);
            if (null != httpResponse) {
                httpResponse = onResponse(request, httpResponse);
                return httpResponse;
            }
        }
        return null;
    }

    public ChannelHandlerContext getNettyClientContext() {
        return nettyClientContext;
    }

    void setNettyClientContext(ChannelHandlerContext nettyClientContext) {
        this.nettyClientContext = nettyClientContext;
    }

    public ChannelHandlerContext getNettyRemoteContext() {
        return nettyRemoteContext;
    }

    void setNettyRemoteContext(ChannelHandlerContext nettyRemoteContext) {
        this.nettyRemoteContext = nettyRemoteContext;
    }

    public Map<String, RequestIntercept> getRequestIntercepts() {
        return requestIntercepts;
    }

    public Map<String, ResponseIntercept> getResponseIntercepts() {
        return responseIntercepts;
    }

    public Channel getClientChannel() {
        return clientChannel;
    }

    void setClientChannel(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public Channel getRemoteChannel() {
        return remoteChannel;
    }

    void setRemoteChannel(Channel remoteChannel) {
        this.remoteChannel = remoteChannel;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    HttpRequest getRequest() {
        return request;
    }

    void setRequest(HttpRequest request) {
        this.request = request;
    }

    FullHttpResponse getFullHttpResponse() {
        return fullHttpResponse;
    }

    void setFullHttpResponse(FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }
}
