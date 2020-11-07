package com.github.supermoonie.proxy.fx.dto;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
public class FlowNode {

    private String id;

    private String url;

    private int status;

    private EnumFlowType type;

    private String contentType;

    private Date requestTime;

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof FlowNode)) {
            return false;
        }
        return Objects.deepEquals(this, obj);
    }
}
