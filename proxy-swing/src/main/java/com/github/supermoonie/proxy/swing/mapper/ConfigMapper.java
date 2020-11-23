package com.github.supermoonie.proxy.swing.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
public interface ConfigMapper {

    @Update("update config set `value` = #{value} where `key` = #{key}")
    Integer updateByKey(@Param("key") String key, @Param("value") String value);
}
