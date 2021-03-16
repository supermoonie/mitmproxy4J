package com.github.supermoonie.proxy.fx.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
class BaseEntity {

    @TableId
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
