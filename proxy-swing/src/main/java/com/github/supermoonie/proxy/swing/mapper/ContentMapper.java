package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.Content;
import com.github.supermoonie.proxy.swing.entity.Request;
import org.apache.ibatis.annotations.Param;

/**
 * @author supermoonie
 * @date 2020-06-07
 */
public interface ContentMapper {

    /**
     * select by id
     *
     * @param id {@link Request#getId()}
     * @return {@link Content}
     */
    Content selectById(@Param("id") String id);

}
