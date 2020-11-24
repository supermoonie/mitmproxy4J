package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.Content;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Content)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-24 12:10:46
 */
public interface ContentMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Content queryById(String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Content> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param content 实例对象
     * @return 对象列表
     */
    List<Content> queryAll(Content content);

    /**
     * 新增数据
     *
     * @param content 实例对象
     * @return 影响行数
     */
    int insert(Content content);

    /**
     * 修改数据
     *
     * @param content 实例对象
     * @return 影响行数
     */
    int update(Content content);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);

}