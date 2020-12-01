package com.github.supermoonie.proxy.swing.gui.flow;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.swing.*;
import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/11/26
 */
public class Flow {

    private Integer requestId;

    private Integer responseId;

    private String url;

    private Integer status;

    private FlowType flowType;

    private String contentType;

    @JsonIgnore
    private Icon icon;

    private Date requestTime;

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public FlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
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
}
