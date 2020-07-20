package com.github.supermoonie.controller.params;

import lombok.Data;

/**
 * @author supermoonie
 * @since 2020/7/15
 */
@Data
public class TextFormData {

    private String name;

    private String value;

    private String fileName;

    private String contentType;

    private String type;
}
