package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysConfig;
import com.unicore.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/config")
public class SysConfigController {

    @Autowired
    private SysConfigMapper configMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysConfig>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String configKey) {
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getValiFlag, "1");
        if (configName != null) {
            wrapper.like(SysConfig::getConfigName, configName);
        }
        if (configKey != null) {
            wrapper.like(SysConfig::getConfigKey, configKey);
        }
        return WrapperResponse.success(configMapper.selectPage(page, wrapper));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysConfig> getById(@PathVariable Integer id) {
        return WrapperResponse.success(configMapper.selectById(id));
    }

    @GetMapping("/key/{key}")
    public WrapperResponse<SysConfig> getByKey(@PathVariable String key) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        wrapper.eq(SysConfig::getValiFlag, "1");
        return WrapperResponse.success(configMapper.selectOne(wrapper));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysConfig config) {
        return WrapperResponse.success(configMapper.insert(config) > 0);
    }

    @PostMapping("/update")
    public WrapperResponse<Boolean> update(@RequestBody SysConfig config) {
        return WrapperResponse.success(configMapper.updateById(config) > 0);
    }

    @GetMapping("/delete/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(configMapper.deleteById(id) > 0);
    }
}
