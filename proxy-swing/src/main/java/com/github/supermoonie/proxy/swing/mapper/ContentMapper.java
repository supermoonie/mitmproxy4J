package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.Content;
import org.apache.ibatis.annotations.Param;

/**
 * @author supermoonie
 * @date 2020-11-22
 */
public interface ContentMapper {

    /**
     * select by id
     *
     * @param id id
     * @return content
     */
    Content selectById(@Param("id") String id);
}
