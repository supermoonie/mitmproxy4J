package com.github.supermoonie.dto;

import com.github.supermoonie.constant.EnumFlowNodeType;
import lombok.Data;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/7/8
 */
@Data
public class FlowNode {

    private String id;

    private String url;

    private EnumFlowNodeType type;

    private Integer status;

    private List<FlowNode> children;
}
