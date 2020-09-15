package com.github.supermoonie.service;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
public interface ConfigService {

    String RECORD_KEY = "RECORD_STATUS";

    String change(String key);

    void initial();

}
