package com.github.supermoonie.proxy.intercept.res;

import com.github.supermoonie.proxy.intercept.AbstractInterceptPipeline;
import com.github.supermoonie.proxy.intercept.InterceptContext;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author supermoonie
 * @date 2020-08-20
 */
public class ResponseInterceptPipeline extends AbstractInterceptPipeline<AbstractResponseIntercept> implements ResponseIntercept {

    @Override
    public void addFirst(AbstractResponseIntercept intercept) {
        if (null == intercept) {
            return;
        }
        if (null == head) {
            head = intercept;
            return;
        }
        AbstractResponseIntercept next = head;
        head = intercept;
        head.next = next;
        next.pre = head;
    }

    @Override
    public void addLast(AbstractResponseIntercept intercept) {
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
        AbstractResponseIntercept pre = tail;
        pre.next = intercept;
        intercept.pre = pre;
        tail = intercept;
    }

    @Override
    public boolean onResponse(InterceptContext ctx, FullHttpResponse response) {
        AbstractResponseIntercept current = head;
        while (null != current) {
            if (!current.onResponse(ctx, response)) {
                return false;
            }
            current = current.next;
        }
        return true;
    }

    @Override
    public boolean onException(InterceptContext ctx, FullHttpResponse response, Exception ex) throws Exception {
        AbstractResponseIntercept current = head;
        while (null != current) {
            if (!current.onException(ctx, response, ex)) {
                return false;
            }
            current = current.next;
        }
        return true;
    }
}
