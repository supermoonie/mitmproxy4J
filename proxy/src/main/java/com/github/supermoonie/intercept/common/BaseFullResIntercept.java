package com.github.supermoonie.intercept.common;

import com.github.supermoonie.intercept.HttpProxyIntercept;
import com.github.supermoonie.intercept.HttpProxyInterceptPipeline;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

/**
 * @author wangc
 */
public abstract class BaseFullResIntercept extends HttpProxyIntercept {

    /**
     * default max content length size is 8MB
     */
    private static final int DEFAULT_MAX_CONTENT_LENGTH = 1024 * 1024 * 8;

    private int maxContentLength;

    public BaseFullResIntercept() {
        this(DEFAULT_MAX_CONTENT_LENGTH);
    }

    public BaseFullResIntercept(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    @Override
    public final void afterResponse(Channel clientChannel, Channel proxyChannel,
                                    HttpResponse httpResponse,
                                    HttpProxyInterceptPipeline pipeline) throws Exception {
        if (httpResponse instanceof FullHttpResponse) {
            FullHttpResponse fullHttpResponse = (FullHttpResponse) httpResponse;
            handelResponse(pipeline.getHttpRequest(), fullHttpResponse, pipeline);
            if (fullHttpResponse.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
                httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
            }
            proxyChannel.pipeline().remove("decompress");
            proxyChannel.pipeline().remove("aggregator");
        } else if (matchHandle(pipeline.getHttpRequest(), pipeline.getHttpResponse(), pipeline)) {
            pipeline.resetAfterHead();
            proxyChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
            proxyChannel.pipeline()
                    .addAfter("decompress", "aggregator", new HttpObjectAggregator(maxContentLength));
            proxyChannel.pipeline().fireChannelRead(httpResponse);
            return;
        }
        pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
    }


    private boolean matchHandle(HttpRequest httpRequest, HttpResponse httpResponse,
                                HttpProxyInterceptPipeline pipeline) {
        boolean isMatch = match(httpRequest, httpResponse, pipeline);
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
            if (fullHttpRequest.content().refCnt() > 0) {
                ReferenceCountUtil.release(fullHttpRequest);
            }
        }
        return isMatch;
    }

    /**
     * 匹配到的响应会解码成FullResponse
     */
    public abstract boolean match(HttpRequest httpRequest, HttpResponse httpResponse,
                                  HttpProxyInterceptPipeline pipeline);

    /**
     * 拦截并处理响应
     */
    public void handelResponse(HttpRequest httpRequest,
                               FullHttpResponse httpResponse,
                               HttpProxyInterceptPipeline pipeline) {
    }
}
