package com.github.supermoonie.proxy.fx.controller;

import com.github.supermoonie.proxy.fx.constant.EnumFlowType;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author supermoonie
 * @since 2020/10/12
 */
public class FlowNode {

    private Integer id;

    private String url;

    private int status;

    private final SimpleIntegerProperty statusProperty = new SimpleIntegerProperty(-1);

    private EnumFlowType type;

    private String contentType;

    private Date requestTime;

    private FlowNode parent;

    private List<FlowNode> children;

    private Boolean expanded;

    private String currentUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getStatusProperty() {
        return statusProperty.get();
    }

    public SimpleIntegerProperty statusPropertyProperty() {
        return statusProperty;
    }

    public void setStatusProperty(int statusProperty) {
        this.statusProperty.set(statusProperty);
    }

    public EnumFlowType getType() {
        return type;
    }

    public void setType(EnumFlowType type) {
        this.type = type;
    }

    public FlowNode getParent() {
        return parent;
    }

    public void setParent(FlowNode parent) {
        this.parent = parent;
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

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FlowNode flowNode = (FlowNode) o;
        return Objects.equals(id, flowNode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
