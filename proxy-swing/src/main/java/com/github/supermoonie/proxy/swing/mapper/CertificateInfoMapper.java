package com.github.supermoonie.proxy.swing.mapper;

import com.github.supermoonie.proxy.swing.entity.CertificateInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (CertificateInfo)表数据库访问层
 *
 * @author makejava
 * @since 2020-11-24 07:58:59
 */
public interface CertificateInfoMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    CertificateInfo queryById(String id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<CertificateInfo> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param certificateInfo 实例对象
     * @return 对象列表
     */
    List<CertificateInfo> queryAll(CertificateInfo certificateInfo);

    /**
     * 新增数据
     *
     * @param certificateInfo 实例对象
     * @return 影响行数
     */
    int insert(CertificateInfo certificateInfo);

    /**
     * 修改数据
     *
     * @param certificateInfo 实例对象
     * @return 影响行数
     */
    int update(CertificateInfo certificateInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(String id);

}