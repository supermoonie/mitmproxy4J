package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.Response;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Response)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-24 12:10:49
 */
public interface ResponseMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Response queryById(String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Response> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param response 实例对象
     * @return 对象列表
     */
    List<Response> queryAll(Response response);

    /**
     * 新增数据
     *
     * @param response 实例对象
     * @return 影响行数
     */
    int insert(Response response);

    /**
     * 修改数据
     *
     * @param response 实例对象
     * @return 影响行数
     */
    int update(Response response);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);

}