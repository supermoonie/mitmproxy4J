package com.github.supermoonie.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/7/5
 */
@Data
@ApiModel
public class FlowDTO {

    private RequestDTO request;

    private List<HeaderDTO> requestHeaders;

    private String requestContent;

    private ResponseDTO response;

    private List<HeaderDTO> responseHeaders;

    private String responseContent;
}
