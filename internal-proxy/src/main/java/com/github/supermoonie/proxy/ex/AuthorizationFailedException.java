package com.github.supermoonie.proxy.ex;

/**
 * @author supermoonie
 * @since 2020/9/6
 */
public class AuthorizationFailedException extends InternalProxyException {

    public AuthorizationFailedException() {
    }

    public AuthorizationFailedException(String message) {
        super(message);
    }

    public AuthorizationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationFailedException(Throwable cause) {
        super(cause);
    }

    public AuthorizationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
