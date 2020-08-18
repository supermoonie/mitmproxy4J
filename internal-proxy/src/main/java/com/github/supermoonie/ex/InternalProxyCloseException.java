package com.github.supermoonie.ex;

/**
 * @author supermoonie
 * @date 2020-08-08
 */
public class InternalProxyCloseException extends InternalProxyException {

    public InternalProxyCloseException() {
        super();
    }

    public InternalProxyCloseException(String message) {
        super(message);
    }

    public InternalProxyCloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalProxyCloseException(Throwable cause) {
        super(cause);
    }

    protected InternalProxyCloseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
