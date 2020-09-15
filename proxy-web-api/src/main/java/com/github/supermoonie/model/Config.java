package com.github.supermoonie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Config extends BaseModel {

    private String key;

    private String value;
}
