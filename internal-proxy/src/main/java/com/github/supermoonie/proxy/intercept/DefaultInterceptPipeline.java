package com.github.supermoonie.proxy.intercept;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public class DefaultInterceptPipeline implements InterceptPipeline {

    private AbstractIntercept head;

    private AbstractIntercept tail;

    @Override
    public InterceptPipeline addFirst(AbstractIntercept intercept) {
        if (null == intercept) {
            return this;
        }
        if (null == head) {
            head = intercept;
            return this;
        }
        AbstractIntercept next = head;
        head = intercept;
        head.next = next;
        return this;
    }

    @Override
    public InterceptPipeline addLast(AbstractIntercept intercept) {
        if (null == intercept) {
            return this;
        }
        if (null == tail) {
            tail = intercept;
            return this;
        }
        AbstractIntercept pre = tail;
        tail = intercept;
        tail.pre = pre;
        return this;
    }

    @Override
    public void onActive(InterceptContext ctx) {
        AbstractIntercept intercept = head;
        while (null != intercept) {
            intercept.onActive(ctx);
            intercept = head.next;
        }
    }

    @Override
    public boolean onRequest(InterceptContext ctx, Object msg) {
        AbstractIntercept intercept = head;
        while (null != intercept) {
            boolean flag = intercept.onRequest(ctx, msg);
            if (!flag) {
                return false;
            }
            intercept = head.next;
        }
        return true;
    }

    @Override
    public void onResponse(InterceptContext ctx, Object msg) {
        AbstractIntercept intercept = head;
        while (null != intercept) {
            intercept.onResponse(ctx, msg);
            intercept = head.next;
        }
    }

    @Override
    public void onException(InterceptContext ctx, Throwable cause) throws Throwable {
        AbstractIntercept intercept = head;
        while (null != intercept) {
            intercept.onException(ctx, cause);
            intercept = head.next;
        }
    }
}
