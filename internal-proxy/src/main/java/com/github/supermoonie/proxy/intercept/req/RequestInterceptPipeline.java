package com.github.supermoonie.proxy.intercept.req;

import com.github.supermoonie.proxy.intercept.AbstractInterceptPipeline;
import com.github.supermoonie.proxy.intercept.InterceptContext;
import com.github.supermoonie.util.ResponseUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public class RequestInterceptPipeline extends AbstractInterceptPipeline<AbstractRequestIntercept> implements RequestIntercept {

    @Override
    public void addFirst(AbstractRequestIntercept intercept) {
        if (null == intercept) {
            return;
        }
        if (null == head) {
            head = intercept;
            return;
        }
        AbstractRequestIntercept next = head;
        head = intercept;
        head.next = next;
        next.pre = head;
    }

    @Override
    public void addLast(AbstractRequestIntercept intercept) {
        if (null == intercept) {
            return;
        }
        if (null == head) {
            head = intercept;
            return;
        }
        if (null == tail) {
            tail = intercept;
            head.next = tail;
            tail.pre = head;
            return;
        }
        AbstractRequestIntercept pre = tail;
        pre.next = intercept;
        intercept.pre = pre;
        tail = intercept;
    }

    @Override
    public FullHttpResponse onRequest(InterceptContext ctx, FullHttpRequest request) {
        AbstractRequestIntercept current = head;
        while (null != current) {
            FullHttpResponse response = current.onRequest(ctx, request);
            if (null != response) {
                ctx.getClientChannel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return null;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public FullHttpResponse onException(InterceptContext ctx, FullHttpRequest request, Exception ex) throws Exception {
        AbstractRequestIntercept current = head;
        while (null != current) {
            FullHttpResponse response = current.onException(ctx, request, ex);
            if (null != response) {
                ctx.getClientChannel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return null;
            }
            current = current.next;
        }
        return null;
    }
}
