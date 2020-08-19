package com.github.supermoonie.constant;

/**
 * @author supermoonie
 * @since 2020/8/9
 */
public enum ConnectionState {

    /**
     * not connection
     */
    NOT_CONNECTION(0),
    /**
     * connecting
     */
    CONNECTING(1),
    /**
     * already connect with client
     */
    CONNECTED_WITH_CLIENT(2),
    /**
     * already connect with remote
     */
    CONNECTED_WITH_REMOTE(3)
    ;

    private final int code;

    ConnectionState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
