package com.github.supermoonie.ex;

/**
 * @author supermoonie
 * @date 2020-08-08
 */
public class InternalProxyException extends RuntimeException {

    public InternalProxyException() {
        super();
    }

    public InternalProxyException(String message) {
        super(message);
    }

    public InternalProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalProxyException(Throwable cause) {
        super(cause);
    }

    protected InternalProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
