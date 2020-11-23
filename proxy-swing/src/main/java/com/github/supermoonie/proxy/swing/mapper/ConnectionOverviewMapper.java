package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.ConnectionOverview;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author supermoonie
 * @date 2020-11-15
 */
public interface ConnectionOverviewMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ConnectionOverview queryById(@Param("id") String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ConnectionOverview> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param connectionOverview 实例对象
     * @return 对象列表
     */
    List<ConnectionOverview> queryAll(ConnectionOverview connectionOverview);

    /**
     * 新增数据
     *
     * @param connectionOverview 实例对象
     * @return 影响行数
     */
    void insert(ConnectionOverview connectionOverview);

    /**
     * 修改数据
     *
     * @param connectionOverview 实例对象
     * @return 影响行数
     */
    int update(ConnectionOverview connectionOverview);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);

}
