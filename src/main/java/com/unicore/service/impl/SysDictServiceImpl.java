package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysDict;
import com.unicore.entity.SysDictType;
import com.unicore.mapper.SysDictMapper;
import com.unicore.mapper.SysDictTypeMapper;
import com.unicore.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典管理服务实现类
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Override
    public Page<SysDictType> selectTypePage(Page<SysDictType> page, String dicTypeName) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dicTypeName)) {
            wrapper.like(SysDictType::getDicTypeName, dicTypeName);
        }
        return dictTypeMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysDictType> selectTypeList() {
        return dictTypeMapper.selectList(null);
    }

    @Override
    public boolean addType(SysDictType dictType) {
        // 检查字典类型编码是否已存在
        if (dictTypeMapper.selectById(dictType.getDicTypeCode()) != null) {
            throw new RuntimeException("字典类型编码已存在");
        }
        return dictTypeMapper.insert(dictType) > 0;
    }

    @Override
    public boolean updateType(SysDictType dictType) {
        return dictTypeMapper.updateById(dictType) > 0;
    }

    @Override
    public boolean deleteType(String code) {
        return dictTypeMapper.deleteById(code) > 0;
    }

    @Override
    public Page<SysDict> selectDataPage(Page<SysDict> page, String dicTypeCode, String dicName, String dicValue) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dicTypeCode)) {
            wrapper.eq(SysDict::getDicTypeCode, dicTypeCode);
        }
        if (StringUtils.hasText(dicName)) {
            wrapper.like(SysDict::getDicName, dicName);
        }
        if (StringUtils.hasText(dicValue)) {
            wrapper.like(SysDict::getDicValue, dicValue);
        }
        wrapper.orderByAsc(SysDict::getOrderNum);
        return page(page, wrapper);
    }

    @Override
    public List<SysDict> selectDataListByTypeCode(String typeCode) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDicTypeCode, typeCode);
        wrapper.orderByAsc(SysDict::getOrderNum);
        return list(wrapper);
    }

    @Override
    public boolean addData(SysDict dict) {
        // 检查同一字典类型下是否存在相同的字典值
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDicTypeCode, dict.getDicTypeCode());
        wrapper.eq(SysDict::getDicValue, dict.getDicValue());
        if (count(wrapper) > 0) {
            throw new RuntimeException("该字典类型下已存在相同的字典值");
        }
        return save(dict);
    }

    @Override
    public boolean updateData(SysDict dict) {
        return updateById(dict);
    }

    @Override
    public boolean deleteData(String id) {
        return removeById(id);
    }
}
