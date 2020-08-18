package com.github.supermoonie.ex;

/**
 * @author supermoonie
 * @date 2020-08-08
 */
public class InternalProxyStartException extends InternalProxyException {

    public InternalProxyStartException() {
        super();
    }

    public InternalProxyStartException(String message) {
        super(message);
    }

    public InternalProxyStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalProxyStartException(Throwable cause) {
        super(cause);
    }

    protected InternalProxyStartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
