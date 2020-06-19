package com.github.supermoonie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Request extends BaseModel {

    private String method;

    private String host;

    private Integer port;

    private String uri;

    private String httpVersion;

    private String contentId;
}
