package com.github.supermoonie.proxy.swing.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @since 2021/1/29
 */
@DatabaseTable(tableName = "host_map")
public class HostMap {

    public static final int DISABLE = 0;
    public static final int ENABLE = 1;
    public static final String HOST_FIELD_NAME = "host";
    public static final String IP_FIELD_NAME = "ip";
    public static final String ENABLE_FIELD_NAME = "enable";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = HOST_FIELD_NAME)
    private String host;
    @DatabaseField(columnName = IP_FIELD_NAME)
    private String ip;
    @DatabaseField(columnName = ENABLE_FIELD_NAME)
    private Integer enable;
    @DatabaseField(columnName = TIME_CREATED_FIELD_NAME, canBeNull = false, index = true, indexName = "idx_time_created")
    private Date timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
}
