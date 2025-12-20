package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysDict;
import com.unicore.entity.SysDictType;
import com.unicore.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys/dict")
public class SysDictController {

    @Autowired
    private SysDictService dictService;

    @GetMapping("/type/page")
    public WrapperResponse<Page<SysDictType>> typePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dicTypeName) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(dictService.selectTypePage(page, dicTypeName));
    }

    @GetMapping("/type/list")
    public WrapperResponse<List<SysDictType>> typeList() {
        return WrapperResponse.success(dictService.selectTypeList());
    }

    @PostMapping("/type")
    public WrapperResponse<?> addType(@RequestBody SysDictType dictType) {
        try {
            return WrapperResponse.success(dictService.addType(dictType));
        } catch (RuntimeException e) {
            return WrapperResponse.fail(e.getMessage(), null);
        }
    }

    @PutMapping("/type")
    public WrapperResponse<Boolean> updateType(@RequestBody SysDictType dictType) {
        return WrapperResponse.success(dictService.updateType(dictType));
    }

    @DeleteMapping("/type/{code}")
    public WrapperResponse<Boolean> deleteType(@PathVariable String code) {
        return WrapperResponse.success(dictService.deleteType(code));
    }

    @GetMapping("/data/page")
    public WrapperResponse<Page<SysDict>> dataPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dicTypeCode,
            @RequestParam(required = false) String dicName,
            @RequestParam(required = false) String dicValue) {
        Page<SysDict> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(dictService.selectDataPage(page, dicTypeCode, dicName, dicValue));
    }

    @GetMapping("/data/list/{typeCode}")
    public WrapperResponse<List<SysDict>> dataList(@PathVariable String typeCode) {
        return WrapperResponse.success(dictService.selectDataListByTypeCode(typeCode));
    }

    @PostMapping("/data")
    public WrapperResponse<?> addData(@RequestBody SysDict dict) {
        try {
            return WrapperResponse.success(dictService.addData(dict));
        } catch (RuntimeException e) {
            return WrapperResponse.fail(e.getMessage(), null);
        }
    }

    @PutMapping("/data")
    public WrapperResponse<Boolean> updateData(@RequestBody SysDict dict) {
        return WrapperResponse.success(dictService.updateData(dict));
    }

    @DeleteMapping("/data/{id}")
    public WrapperResponse<Boolean> deleteData(@PathVariable String id) {
        return WrapperResponse.success(dictService.deleteData(id));
    }
}
