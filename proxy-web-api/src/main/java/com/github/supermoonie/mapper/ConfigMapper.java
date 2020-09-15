package com.github.supermoonie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.supermoonie.model.Config;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author supermoonie
 * @since 2020/9/15
 */
public interface ConfigMapper extends BaseMapper<Config> {

    @Update("update config set `value` = #{value} where `key` = #{key}")
    Integer updateByKey(@Param("key") String key, @Param("value") String value);
}
