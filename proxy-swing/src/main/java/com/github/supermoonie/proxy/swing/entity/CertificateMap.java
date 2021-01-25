package com.github.supermoonie.proxy.swing.entity;

import com.github.supermoonie.proxy.swing.util.Jackson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-11-16
 */
@DatabaseTable(tableName = "certificate_map")
public class CertificateMap {

    public static final String CERTIFICATE_SERIAL_NUMBER_FIELD_NAME = "certificate_serial_number";
    public static final String REQUEST_ID_FIELD_NAME = "request_id";
    public static final String RESPONSE_ID_FIELD_NAME = "response_id";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = CERTIFICATE_SERIAL_NUMBER_FIELD_NAME, canBeNull = false)
    private String certificateSerialNumber;
    @DatabaseField(columnName = REQUEST_ID_FIELD_NAME, canBeNull = false)
    private Integer requestId;
    @DatabaseField(columnName = RESPONSE_ID_FIELD_NAME)
    private Integer responseId;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    public void setCertificateSerialNumber(String certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
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
        return "CertificateMap{" +
                "id=" + id +
                ", certificateSerialNumber='" + certificateSerialNumber + '\'' +
                ", requestId=" + requestId +
                ", responseId=" + responseId +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
