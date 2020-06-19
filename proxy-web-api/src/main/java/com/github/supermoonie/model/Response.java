package com.github.supermoonie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Response extends BaseModel {

    private String requestId;

    private String httpVersion;

    private Integer status;

    private String contentType;

    private String contentId;
}
