package com.github.supermoonie.proxy.swing.entity;

import java.util.Arrays;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
public class Content extends BaseEntity {

    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Content{" +
                "content=" + Arrays.toString(content) +
                '}';
    }
}
