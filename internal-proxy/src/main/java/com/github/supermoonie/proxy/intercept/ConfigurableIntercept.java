package com.github.supermoonie.proxy.intercept;


import java.util.List;
import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/8/15
 */
public class ConfigurableIntercept {

    private List<String> blackList;

    private List<String> whiteList;

    private Map<String, String> remoteMap;

    private Map<String, String> localMap;

    private String userName;

    private String password;
}
