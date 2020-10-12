package com.github.supermoonie.proxy.fx.entity;

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

    @Override
    public String toString() {
        return "Response{" +
                "requestId='" + requestId + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", status=" + status +
                ", contentType='" + contentType + '\'' +
                ", contentId='" + contentId + '\'' +
                '}';
    }
}
