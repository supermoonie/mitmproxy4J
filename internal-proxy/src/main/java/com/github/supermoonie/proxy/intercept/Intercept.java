package com.github.supermoonie.proxy.intercept;

/**
 * @author supermoonie
 * @since 2020/8/11
 */
public interface Intercept {

    /**
     * on active
     *
     * @param ctx {@link InterceptContext}
     */
    void onActive(InterceptContext ctx);

    /**
     * on request
     *
     * @param ctx {@link InterceptContext}
     * @param msg request msg
     * @return true continue, false break
     */
    boolean onRequest(InterceptContext ctx, Object msg);

    /**
     * on response
     *
     * @param ctx {@link InterceptContext}
     * @param msg response msg
     */
    void onResponse(InterceptContext ctx, Object msg);

    /**
     * on exception
     *
     * @param ctx   {@link InterceptContext}
     * @param cause {@link Throwable}
     * @throws Throwable t
     */
    void onException(InterceptContext ctx, Throwable cause) throws Throwable;
}
