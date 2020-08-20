package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.ConnectionInfo;
import com.github.supermoonie.proxy.intercept.req.RequestInterceptPipeline;
import com.github.supermoonie.proxy.intercept.res.ResponseInterceptPipeline;
import io.netty.channel.Channel;

/**
 * @author supermoonie
 * @since 2020/8/12
 */
public class InterceptContext {

    private Channel clientChannel;

    private Channel remoteChannel;

    private ConnectionInfo connectionInfo;

    private final RequestInterceptPipeline requestInterceptPipeline;

    private final ResponseInterceptPipeline responseInterceptPipeline;

    public InterceptContext(RequestInterceptPipeline requestInterceptPipeline, ResponseInterceptPipeline responseInterceptPipeline) {
        this.requestInterceptPipeline = requestInterceptPipeline;
        this.responseInterceptPipeline = responseInterceptPipeline;
    }

    public RequestInterceptPipeline getRequestInterceptPipeline() {
        return requestInterceptPipeline;
    }

    public ResponseInterceptPipeline getResponseInterceptPipeline() {
        return responseInterceptPipeline;
    }

    public Channel getClientChannel() {
        return clientChannel;
    }

    public void setClientChannel(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public Channel getRemoteChannel() {
        return remoteChannel;
    }

    public void setRemoteChannel(Channel remoteChannel) {
        this.remoteChannel = remoteChannel;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
