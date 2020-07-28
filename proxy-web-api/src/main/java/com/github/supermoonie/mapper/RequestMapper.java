package com.github.supermoonie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.supermoonie.mapper.dao.SimpleRequestDAO;
import com.github.supermoonie.model.Request;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
public interface RequestMapper extends BaseMapper<Request> {

    @Select("<script>select request.id, request.uri, response.status " +
            "from request left join response " +
            "on request.id = response.requestId where 1 = 1 " +
            "<if test='host != null'>and request.host like '%' || #{host} || '%' </if>" +
            "<if test='method != null'>and request.method = #{method} </if>" +
            "<if test='start != null'>and request.timeCreated &gt; #{start} </if>" +
            "<if test='end != null'>and request.timeCreated &lt;= #{end} </if>" +
            "order by request.timeCreated desc" +
            "</script>")
    List<SimpleRequestDAO> selectSimple(@Param("host") String host, @Param("method") String method,
                                        @Param("start") String start, @Param("end") String end);
}
