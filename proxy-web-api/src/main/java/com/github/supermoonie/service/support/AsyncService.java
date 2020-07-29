package com.github.supermoonie.service.support;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author supermoonie
 * @date 2020-07-29
 */
@Component
@Async
public class AsyncService {

    public void execute(SimpleConsumer consumer) {
        consumer.apply();
    }
}
