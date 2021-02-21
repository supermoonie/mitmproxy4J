package com.github.supermoonie.proxy.fx.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
@DatabaseTable(tableName = "request")
public class Request {

    public static final String METHOD_FIELD_NAME = "method";
    public static final String HOST_FIELD_NAME = "host";
    public static final String PORT_FIELD_NAME = "port";
    public static final String URI_FIELD_NAME = "uri";
    public static final String HTTP_VERSION_FIELD_NAME = "http_version";
    public static final String CONTENT_TYPE_FIELD_NAME = "content_type";
    public static final String CONTENT_ID_FIELD_NAME = "content_id";
    public static final String START_TIME_FIELD_NAME = "start_time";
    public static final String END_TIME_FIELD_NAME = "end_time";
    public static final String SIZE_FIELD_NAME = "size";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = METHOD_FIELD_NAME)
    private String method;
    @DatabaseField(columnName = HOST_FIELD_NAME)
    private String host;
    @DatabaseField(columnName = PORT_FIELD_NAME)
    private Integer port;
    @DatabaseField(columnName = URI_FIELD_NAME, index = true, indexName = "idx_uri")
    private String uri;
    @DatabaseField(columnName = HTTP_VERSION_FIELD_NAME)
    private String httpVersion;
    @DatabaseField(columnName = CONTENT_TYPE_FIELD_NAME)
    private String contentType;
    @DatabaseField(columnName = CONTENT_ID_FIELD_NAME)
    private Integer contentId;
    @DatabaseField(columnName = START_TIME_FIELD_NAME)
    private Long startTime;
    @DatabaseField(columnName = END_TIME_FIELD_NAME)
    private Long endTime;
    @DatabaseField(columnName = SIZE_FIELD_NAME)
    private Integer size;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", uri='" + uri + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentId=" + contentId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", size=" + size +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
