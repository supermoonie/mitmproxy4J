package com.github.supermoonie.intercept.common;

import com.github.supermoonie.intercept.HttpProxyIntercept;
import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

/**
 * @author wangc
 */
public abstract class BaseFullReqIntercept extends HttpProxyIntercept {

    /**
     * default max content length size is 8MB
     */
    private static final int DEFAULT_MAX_CONTENT_LENGTH = 1024 * 1024 * 8;

    private int maxContentLength;

    public BaseFullReqIntercept() {
        this(DEFAULT_MAX_CONTENT_LENGTH);
    }

    public BaseFullReqIntercept(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }


    @Override
    public final void beforeRequest(Channel clientChannel, HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) throws Exception {
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
            handelRequest(fullHttpRequest, pipeline);
            fullHttpRequest.content().markReaderIndex();
            fullHttpRequest.content().retain();
            if (fullHttpRequest.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
                fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
            }
        } else if (match(httpRequest, pipeline)) {
            //重置拦截器
            pipeline.resetBeforeHead();
            //添加gzip解压处理
            clientChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
            //添加Full request解码器
            clientChannel.pipeline().addAfter("decompress", "aggregator", new HttpObjectAggregator(DEFAULT_MAX_CONTENT_LENGTH));
            //重新过一遍处理器链
            clientChannel.pipeline().fireChannelRead(httpRequest);
            return;
        }
        pipeline.beforeRequest(clientChannel, httpRequest);
    }

    @Override
    public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {
        //如果是FullHttpRequest
        if (pipeline.getHttpRequest() instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) pipeline.getHttpRequest();
            httpRequest.content().resetReaderIndex();
        }
        pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
    }

    /**
     * 匹配到的请求会解码成FullRequest
     */
    public abstract boolean match(HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline);

    /**
     * 拦截并处理响应
     */
    public void handelRequest(FullHttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
    }
}
