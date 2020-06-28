package com.github.supermoonie.controller.vo;

import com.github.supermoonie.collection.LinkedMultiObjectValueMap;
import lombok.Data;

/**
 * @author supermoonie
 * @since 2020/6/25
 */
@Data
public class HttpRequestVO {

    private String url;

    private LinkedMultiObjectValueMap<String> args;

    private LinkedMultiObjectValueMap<String> headers;

    private LinkedMultiObjectValueMap<String> form;

    private String body;
}
