package com.github.supermoonie.exception;

import io.netty.channel.Channel;

/**
 * 异常处理器
 *
 * @author wangc
 */
public class HttpProxyExceptionHandle {

    public void beforeCatch(Channel clientChannel, Throwable cause) throws Exception {
        throw new Exception(cause);
    }

    public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause)
            throws Exception {
        throw new Exception(cause);
    }
}
