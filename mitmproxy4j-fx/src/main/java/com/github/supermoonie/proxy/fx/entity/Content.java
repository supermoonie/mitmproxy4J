package com.github.supermoonie.proxy.fx.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Arrays;
import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
@DatabaseTable(tableName = "content")
public class Content {

    public static final String RAW_CONTENT_FIELD_NAME = "raw_content";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = RAW_CONTENT_FIELD_NAME, dataType = DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] rawContent;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getRawContent() {
        return rawContent;
    }

    public void setRawContent(byte[] rawContent) {
        this.rawContent = rawContent;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", rawContent=" + Arrays.toString(rawContent) +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
