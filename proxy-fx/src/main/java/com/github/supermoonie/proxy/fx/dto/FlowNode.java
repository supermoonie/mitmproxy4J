package com.github.supermoonie.proxy.fx.dto;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;

import java.util.List;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
public class FlowNode {

    private String id;

    private String url;

    private int status;

    private EnumFlowType type;

    private List<FlowNode> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public EnumFlowType getType() {
        return type;
    }

    public void setType(EnumFlowType type) {
        this.type = type;
    }

    public List<FlowNode> getChildren() {
        return children;
    }

    public void setChildren(List<FlowNode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "FlowNode{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", children=" + children +
                '}';
    }
}
