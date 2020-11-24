package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.Header;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Header)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-24 12:10:48
 */
public interface HeaderMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Header queryById(String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Header> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param header 实例对象
     * @return 对象列表
     */
    List<Header> queryAll(Header header);

    /**
     * 新增数据
     *
     * @param header 实例对象
     * @return 影响行数
     */
    int insert(Header header);

    /**
     * 修改数据
     *
     * @param header 实例对象
     * @return 影响行数
     */
    int update(Header header);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);

}