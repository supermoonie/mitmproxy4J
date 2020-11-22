package com.github.supermoonie.proxy.swing.entity;

import java.util.Arrays;
import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
public class Content {

    private String id;

    private byte[] content;

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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", content=" + Arrays.toString(content) +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
