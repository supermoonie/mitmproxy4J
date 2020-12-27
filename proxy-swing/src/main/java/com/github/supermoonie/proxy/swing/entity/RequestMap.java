package com.github.supermoonie.proxy.swing.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author supermoonie
 * @since 2020/12/27
 */
@DatabaseTable(tableName = "request_map")
public class RequestMap {

    public static final int TYPE_REMOTE = 0;
    public static final int TYPE_LOCAL = 1;
    public static final int LOCAL_MAP_FILE = 0;
    public static final int LOCAL_MAP_DIRECTORY = 1;
    public static final int DISABLE = 0;
    public static final int ENABLE = 1;

    public static final String FROM_URL_FIELD_NAME = "from_url";
    public static final String TO_URL_FIELD_NAME = "to_url";
    public static final String MAP_TYPE_FIELD_NAME = "map_type";
    public static final String LOCAL_MAP_TYPE_FIELD_NAME = "local_map_type";
    public static final String ENABLE_FIELD_NAME = "enable";
    public static final String TIME_CREATED_FIELD_NAME = "time_created";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = FROM_URL_FIELD_NAME, canBeNull = false)
    private String fromUrl;
    @DatabaseField(columnName = TO_URL_FIELD_NAME, canBeNull = false)
    private String toUrl;
    @DatabaseField(columnName = MAP_TYPE_FIELD_NAME, canBeNull = false)
    private Integer mapType;
    @DatabaseField(columnName = LOCAL_MAP_TYPE_FIELD_NAME)
    private Integer localMapType;
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

    public String getFromUrl() {
        return fromUrl;
    }

    public void setFromUrl(String fromUrl) {
        this.fromUrl = fromUrl;
    }

    public String getToUrl() {
        return toUrl;
    }

    public void setToUrl(String toUrl) {
        this.toUrl = toUrl;
    }

    public Integer getMapType() {
        return mapType;
    }

    public void setMapType(Integer mapType) {
        this.mapType = mapType;
    }

    public Integer getLocalMapType() {
        return localMapType;
    }

    public void setLocalMapType(Integer localMapType) {
        this.localMapType = localMapType;
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
        return "RequestMap{" +
                "id=" + id +
                ", fromUrl='" + fromUrl + '\'' +
                ", toUrl='" + toUrl + '\'' +
                ", mapType=" + mapType +
                ", localMapType=" + localMapType +
                ", enable=" + enable +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
