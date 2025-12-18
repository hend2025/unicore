package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysPara;
import com.unicore.mapper.SysParaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/para")
public class SysParaController {

    @Autowired
    private SysParaMapper paraMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysPara>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String paraName,
            @RequestParam(required = false) String paraCode) {
        Page<SysPara> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPara> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPara::getValiFlag, "1");
        if (paraName != null) {
            wrapper.like(SysPara::getParaName, paraName);
        }
        if (paraCode != null) {
            wrapper.like(SysPara::getParaCode, paraCode);
        }
        return WrapperResponse.success(paraMapper.selectPage(page, wrapper));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysPara> getById(@PathVariable Integer id) {
        return WrapperResponse.success(paraMapper.selectById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysPara para) {
        return WrapperResponse.success(paraMapper.insert(para) > 0);
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysPara para) {
        return WrapperResponse.success(paraMapper.updateById(para) > 0);
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(paraMapper.deleteById(id) > 0);
    }
}
