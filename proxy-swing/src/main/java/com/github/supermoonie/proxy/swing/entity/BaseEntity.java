package com.github.supermoonie.proxy.swing.entity;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
class BaseEntity {

    private String id;

    private Date timeCreated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "id='" + id + '\'' +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
