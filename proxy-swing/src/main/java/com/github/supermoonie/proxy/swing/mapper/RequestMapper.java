package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.Request;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Request)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-24 12:10:48
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

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Request queryById(String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Request> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param request 实例对象
     * @return 对象列表
     */
    List<Request> queryAll(Request request);

    /**
     * 新增数据
     *
     * @param request 实例对象
     * @return 影响行数
     */
    int insert(Request request);

    /**
     * 修改数据
     *
     * @param request 实例对象
     * @return 影响行数
     */
    int update(Request request);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);

}