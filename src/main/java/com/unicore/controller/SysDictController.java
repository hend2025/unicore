package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysDict;
import com.unicore.entity.SysDictType;
import com.unicore.mapper.SysDictMapper;
import com.unicore.mapper.SysDictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys/dict")
public class SysDictController {

    @Autowired
    private SysDictMapper dictMapper;

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @GetMapping("/type/page")
    public WrapperResponse<Page<SysDictType>> typePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dicTypeName) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (dicTypeName != null && !dicTypeName.isEmpty()) {
            wrapper.like(SysDictType::getDicTypeName, dicTypeName);
        }
        return WrapperResponse.success(dictTypeMapper.selectPage(page, wrapper));
    }

    @GetMapping("/type/list")
    public WrapperResponse<List<SysDictType>> typeList() {
        return WrapperResponse.success(dictTypeMapper.selectList(null));
    }

    @PostMapping("/type")
    public WrapperResponse<?> addType(@RequestBody SysDictType dictType) {
        // 检查字典类型编码是否已存在
        if (dictTypeMapper.selectById(dictType.getDicTypeCode()) != null) {
            return WrapperResponse.fail("字典类型编码已存在", null);
        }
        return WrapperResponse.success(dictTypeMapper.insert(dictType) > 0);
    }

    @PutMapping("/type")
    public WrapperResponse<Boolean> updateType(@RequestBody SysDictType dictType) {
        return WrapperResponse.success(dictTypeMapper.updateById(dictType) > 0);
    }

    @DeleteMapping("/type/{code}")
    public WrapperResponse<Boolean> deleteType(@PathVariable String code) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(dictTypeMapper.deleteById(code) > 0);
    }

    @GetMapping("/data/page")
    public WrapperResponse<Page<SysDict>> dataPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dicTypeCode,
            @RequestParam(required = false) String dicName,
            @RequestParam(required = false) String dicValue) {
        Page<SysDict> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (dicTypeCode != null && !dicTypeCode.isEmpty()) {
            wrapper.eq(SysDict::getDicTypeCode, dicTypeCode);
        }
        if (dicName != null && !dicName.isEmpty()) {
            wrapper.like(SysDict::getDicName, dicName);
        }
        if (dicValue != null && !dicValue.isEmpty()) {
            wrapper.like(SysDict::getDicValue, dicValue);
        }
        wrapper.orderByAsc(SysDict::getOrderNum);
        return WrapperResponse.success(dictMapper.selectPage(page, wrapper));
    }

    @GetMapping("/data/list/{typeCode}")
    public WrapperResponse<List<SysDict>> dataList(@PathVariable String typeCode) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDicTypeCode, typeCode);
        wrapper.orderByAsc(SysDict::getOrderNum);
        return WrapperResponse.success(dictMapper.selectList(wrapper));
    }

    @PostMapping("/data")
    public WrapperResponse<?> addData(@RequestBody SysDict dict) {
        // 检查同一字典类型下是否存在相同的字典值
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDicTypeCode, dict.getDicTypeCode());
        wrapper.eq(SysDict::getDicValue, dict.getDicValue());
        if (dictMapper.selectCount(wrapper) > 0) {
            return WrapperResponse.fail("该字典类型下已存在相同的字典值", null);
        }
        return WrapperResponse.success(dictMapper.insert(dict) > 0);
    }

    @PutMapping("/data")
    public WrapperResponse<Boolean> updateData(@RequestBody SysDict dict) {
        return WrapperResponse.success(dictMapper.updateById(dict) > 0);
    }

    @DeleteMapping("/data/{id}")
    public WrapperResponse<Boolean> deleteData(@PathVariable String id) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(dictMapper.deleteById(id) > 0);
    }
}
