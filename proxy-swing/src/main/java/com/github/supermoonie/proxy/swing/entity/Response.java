package com.github.supermoonie.proxy.swing.entity;

import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
@DatabaseTable(tableName = "response")
public class Response {

    public static final String REQUEST_ID_FIELD_NAME = "request_id";
    public static final String HTTP_VERSION_FIELD_NAME = "http_version";
    public static final String STATUS_FIELD_NAME = "status";
    public static final String CONTENT_TYPE_FIELD_NAME = "content_type";
    public static final String CONTENT_ID_FIELD_NAME = "content_id";
    public static final String START_TIME_FIELD_NAME = "start_time";
    public static final String EDN_TIME_FIELD_NAME = "end_time";
    public static final String SIZE_FIELD_NAME = "size";
    public static final String TIME_CREATED_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = REQUEST_ID_FIELD_NAME, unique = true, uniqueIndexName = "uk_request_id")
    private Integer requestId;
    @DatabaseField(columnName = HTTP_VERSION_FIELD_NAME)
    private String httpVersion;
    @DatabaseField(columnName = STATUS_FIELD_NAME)
    private Integer status;
    @DatabaseField(columnName = CONTENT_TYPE_FIELD_NAME)
    private String contentType;
    @DatabaseField(columnName = CONTENT_ID_FIELD_NAME)
    private Integer contentId;
    @DatabaseField(columnName = START_TIME_FIELD_NAME)
    private Long startTime;
    @DatabaseField(columnName = EDN_TIME_FIELD_NAME)
    private Long endTime;
    @DatabaseField(columnName = SIZE_FIELD_NAME)
    private Integer size;
    @DatabaseField(columnName = TIME_CREATED_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
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

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}
