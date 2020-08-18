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
     * already handshake with client
     */
    ALREADY_HANDSHAKE_WITH_CLIENT(2),
    /**
     * connected
     */
    CONNECTED(3)
    ;

    private final int code;

    ConnectionState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
