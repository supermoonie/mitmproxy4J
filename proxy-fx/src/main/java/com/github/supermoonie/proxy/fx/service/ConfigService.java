package com.github.supermoonie.proxy.fx.service;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
public interface ConfigService {

    String RECORD_KEY = "RECORD_STATUS";
    String THROTTLING_KEY = "THROTTLING_STATUS";
    String THROTTLING_READ_LIMIT = "THROTTLING_READ_LIMIT";
    String THROTTLING_WRITE_LIMIT = "THROTTLING_WRITE_LIMIT";

    String change(String key);

//    String throttlingSetting(ThrottlingSetting setting);

    String switchThrottling();

    void initial();

}
