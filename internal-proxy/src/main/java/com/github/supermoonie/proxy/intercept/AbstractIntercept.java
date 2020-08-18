package com.github.supermoonie.proxy.intercept;

/**
 * @author supermoonie
 * @since 2020/8/12
 */
public abstract class AbstractIntercept implements Intercept {

    AbstractIntercept pre;

    AbstractIntercept next;

    @Override
    public void onActive(InterceptContext ctx) {
        if (null != next) {
            next.onActive(ctx);
        }
    }

    @Override
    public boolean onRequest(InterceptContext ctx, Object msg) {
        if (null != next) {
            return next.onRequest(ctx, msg);
        }
        return true;
    }

    @Override
    public void onResponse(InterceptContext ctx, Object msg) {
        Intercept intercept = next;
        if (null != intercept) {
            intercept.onResponse(ctx, msg);
        }
    }

    @Override
    public void onException(InterceptContext ctx, Throwable cause) throws Throwable {
        Intercept intercept = next;
        if (null != intercept) {
            intercept.onException(ctx, cause);
        }
    }
}
