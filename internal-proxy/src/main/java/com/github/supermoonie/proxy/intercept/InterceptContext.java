package com.github.supermoonie.proxy.intercept;

import com.github.supermoonie.proxy.ConnectionInfo;
import io.netty.channel.Channel;

/**
 * @author supermoonie
 * @since 2020/8/12
 */
public class InterceptContext {

    public InterceptContext(InterceptPipeline pipeline) {
        this.pipeline = pipeline;
    }

    private Channel clientChannel;

    private Channel remoteChannel;

    private ConnectionInfo connectionInfo;

    private InterceptPipeline pipeline;

    public InterceptPipeline getPipeline() {
        return pipeline;
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
