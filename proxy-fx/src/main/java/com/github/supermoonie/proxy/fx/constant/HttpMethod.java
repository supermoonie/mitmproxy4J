package com.github.supermoonie.proxy.fx.constant;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/10/24
 */
public interface HttpMethod {

    String GET = "GET";
    String HEAD = "HEAD";
    String POST = "POST";
    String PUT = "PUT";
    String PATCH = "PATCH";
    String DELETE = "DELETE";
    String OPTIONS = "OPTIONS";
    String TRACE = "TRACE";

    List<String> ALL_METHOD = List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE");
}
