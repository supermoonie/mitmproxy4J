package com.github.supermoonie.dto;

import lombok.Data;

/**
 * @author supermoonie
 * @since 2020/7/5
 */
@Data
public class RequestDTO {

    private String id;

    private Long timeCreated;

    private String method;

    private String host;

    private Integer port;

    private String uri;

    private String httpVersion;

    private String contentType;

    private String contentId;
}
