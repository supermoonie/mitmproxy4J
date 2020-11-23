package com.github.supermoonie.proxy.swing.mapper;

/**
 * @author supermoonie
 * @date 2020-05-30
 */
public interface RequestMapper {

//    @Select("<script>select request.id, request.uri, response.status " +
//            "from request left join response " +
//            "on request.id = response.request_id where 1 = 1 " +
//            "<if test='host != null'>and request.host like '%' || #{host} || '%' </if>" +
//            "<if test='method != null'>and request.method = #{method} </if>" +
//            "<if test='start != null'>and request.time_created &gt; #{start} </if>" +
//            "<if test='end != null'>and request.time_created &lt;= #{end} </if>" +
//            "order by request.time_created desc" +
//            "</script>")
//    List<SimpleRequestDTO> selectSimple(@Param("host") String host, @Param("method") String method,
//                                        @Param("start") String start, @Param("end") String end);
}
