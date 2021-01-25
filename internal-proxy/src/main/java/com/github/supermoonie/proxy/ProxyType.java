package com.github.supermoonie.proxy;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author supermoonie
 * @since 2020/9/8
 */
public enum ProxyType {

    /**
     * proxy type
     */
    HTTP(0), SOCKS4(1), SOCKS5(2);

    private final int code;

    ProxyType(int code) {
        this.code = code;
    }

    public static Optional<ProxyType> valueOf(int code) {
        return Stream.of(ProxyType.values()).filter(item -> item.code == code).findFirst();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ProxyType{" +
                "code=" + code +
                '}';
    }
}
