package com.github.supermoonie.proxy.fx.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
@DatabaseTable(tableName = "header")
public class Header {

    public static final String NAME_FIELD_NAME = "name";
    public static final String VALUE_FIELD_NAME = "value";
    public static final String REQUEST_ID_FIELD_NAME = "request_id";
    public static final String RESPONSE_ID_FIELD_NAME = "response_id";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
    private String name;
    @DatabaseField(columnName = VALUE_FIELD_NAME, canBeNull = false)
    private String value;
    @DatabaseField(columnName = REQUEST_ID_FIELD_NAME, index = true, indexName = "idx_request_id")
    private Integer requestId;
    @DatabaseField(columnName = RESPONSE_ID_FIELD_NAME, index = true, indexName = "idx_response_id")
    private Integer responseId;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "Header{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", requestId=" + requestId +
                ", responseId=" + responseId +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
