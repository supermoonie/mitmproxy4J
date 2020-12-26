package com.github.supermoonie.proxy.swing.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/12/26
 */
@DatabaseTable(tableName = "allow_block")
public class AllowBlock {

    public static final int TYPE_BLOCK = 0;
    public static final int TYPE_ALLOW = 1;
    public static final int DISABLE = 0;
    public static final int ENABLE = 1;

    public static final String LOCATION_FIELD_NAME = "location";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";
    public static final String TYPE_FIELD_NAME = "type";
    public static final String ENABLE_FIELD_NAME = "enable";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = LOCATION_FIELD_NAME, canBeNull = false)
    private String location;
    @DatabaseField(columnName = TYPE_FIELD_NAME, canBeNull = false)
    private Integer type;
    @DatabaseField(columnName = ENABLE_FIELD_NAME, canBeNull = false)
    private Integer enable;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "AllowBlock{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", enable=" + enable +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
