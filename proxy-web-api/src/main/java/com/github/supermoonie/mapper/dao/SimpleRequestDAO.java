package com.github.supermoonie.mapper.dao;

import com.github.supermoonie.model.Request;
import com.github.supermoonie.model.Response;
import lombok.Data;

/**
 * @author supermoonie
 * @since 2020/7/9
 */
@Data
public class SimpleRequestDAO {

    /**
     * @see Request#getId()
     */
    private String id;

    /**
     * @see Request#getUri()
     */
    private String uri;

    /**
     * @see Response#getStatus()
     */
    private Integer status;
}
