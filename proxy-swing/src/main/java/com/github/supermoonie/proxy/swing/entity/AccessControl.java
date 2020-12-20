package com.github.supermoonie.proxy.swing.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/12/20
 */
@DatabaseTable(tableName = "access_control")
public class AccessControl {

    public static final String ACCESS_IP_FIELD_NAME = "access_ip";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = ACCESS_IP_FIELD_NAME, canBeNull = false)
    private String accessIp;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccessIp() {
        return accessIp;
    }

    public void setAccessIp(String accessIp) {
        this.accessIp = accessIp;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "AccessControl{" +
                "id=" + id +
                ", accessIp='" + accessIp + '\'' +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
