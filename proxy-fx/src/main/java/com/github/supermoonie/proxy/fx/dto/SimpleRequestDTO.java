package com.github.supermoonie.proxy.fx.dto;

/**
 * @author supermoonie
 * @since 2020/7/9
 */
public class SimpleRequestDTO {

    /**
     * @see com.github.supermoonie.proxy.fx.entity.Request#getId()
     */
    private String id;

    /**
     * @see com.github.supermoonie.proxy.fx.entity.Request#getUri()
     */
    private String uri;

    /**
     * @see com.github.supermoonie.proxy.fx.entity.Response#getStatus()
     */
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SimpleRequestDTO{" +
                "id='" + id + '\'' +
                ", uri='" + uri + '\'' +
                ", status=" + status +
                '}';
    }
}
