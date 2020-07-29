package com.github.supermoonie.ws.request;

import lombok.Data;

import java.util.Date;

/**
 * @author supermoonie
 * @date 2020-07-28
 */
@Data
public class FetchRequest {

    private String host;

    private String method;

    private Date start;

    private Date end;
}
