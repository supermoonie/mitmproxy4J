package com.github.supermoonie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Content extends BaseModel {

    private byte[] content;
}
