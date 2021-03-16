package com.github.supermoonie.proxy.fx.entity;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
public class Response extends BaseEntity {

    private String requestId;

    private String httpVersion;

    private Integer status;

    private String contentType;

    private String contentId;

    private Long startTime;

    private Long endTime;

    private Integer size;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestId='" + requestId + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", status=" + status +
                ", contentType='" + contentType + '\'' +
                ", contentId='" + contentId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", size=" + size +
                '}';
    }
}
