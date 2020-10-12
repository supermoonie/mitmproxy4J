package com.github.supermoonie.proxy.fx.entity;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
public class Request extends BaseEntity {

    private String method;

    private String host;

    private Integer port;

    private String uri;

    private String httpVersion;

    private String contentType;

    private String contentId;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
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
        return "Request{" +
                "method='" + method + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", uri='" + uri + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentId='" + contentId + '\'' +
                '}';
    }
}
