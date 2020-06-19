package com.github.supermoonie.proxy;

import com.github.supermoonie.exception.HttpProxyExceptionHandle;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author supermoonie
 * @date 2020-06-09
 */
@Component
@Slf4j
public class DefaultHttpProxyExceptionHandle extends HttpProxyExceptionHandle {

    @Override
    public void beforeCatch(Channel clientChannel, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }

    @Override
    public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
    }
}
