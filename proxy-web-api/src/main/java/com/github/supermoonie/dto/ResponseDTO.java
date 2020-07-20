package com.github.supermoonie.dto;

import lombok.Data;

/**
 * @author supermoonie
 * @since 2020/7/6
 */
@Data
public class ResponseDTO {

    private String id;

    private Long timeCreated;

    private String requestId;

    private String httpVersion;

    private Integer status;

    private String contentType;

    private String contentId;
}
