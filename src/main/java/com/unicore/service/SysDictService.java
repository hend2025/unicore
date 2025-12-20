package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysDict;
import com.unicore.entity.SysDictType;

import java.util.List;

/**
 * 字典管理服务接口
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 分页查询字典类型
     */
    Page<SysDictType> selectTypePage(Page<SysDictType> page, String dicTypeName);

    /**
     * 查询所有字典类型
     */
    List<SysDictType> selectTypeList();

    /**
     * 新增字典类型
     */
    boolean addType(SysDictType dictType);

    /**
     * 更新字典类型
     */
    boolean updateType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    boolean deleteType(String code);

    /**
     * 分页查询字典数据
     */
    Page<SysDict> selectDataPage(Page<SysDict> page, String dicTypeCode, String dicName, String dicValue);

    /**
     * 根据字典类型编码查询字典数据列表
     */
    List<SysDict> selectDataListByTypeCode(String typeCode);

    /**
     * 新增字典数据
     */
    boolean addData(SysDict dict);

    /**
     * 更新字典数据
     */
    boolean updateData(SysDict dict);

    /**
     * 删除字典数据
     */
    boolean deleteData(String id);
}
