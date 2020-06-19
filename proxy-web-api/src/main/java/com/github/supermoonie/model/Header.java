package com.github.supermoonie.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Header extends BaseModel {

    private String name;

    private String value;

    private String requestId;

    private String responseId;

    private Date timeCreated;
}
