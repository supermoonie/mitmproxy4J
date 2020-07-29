package com.github.supermoonie.config.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * @author supermoonie
 * @date 2020-07-29
 */
@Slf4j
public class ListenChannelInterceptorAdapter implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return null;
    }
}
