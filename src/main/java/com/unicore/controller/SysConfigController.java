package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysConfig;
import com.unicore.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/config")
public class SysConfigController {

    @Autowired
    private SysConfigService configService;

    @GetMapping("/page")
    public WrapperResponse<Page<SysConfig>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String configKey) {
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(configService.selectPage(page, configName, configKey));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysConfig> getById(@PathVariable Integer id) {
        return WrapperResponse.success(configService.getById(id));
    }

    @GetMapping("/key/{key}")
    public WrapperResponse<SysConfig> getByKey(@PathVariable String key) {
        return WrapperResponse.success(configService.selectByKey(key));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysConfig config) {
        return WrapperResponse.success(configService.addConfig(config));
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysConfig config) {
        return WrapperResponse.success(configService.updateConfig(config));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        return WrapperResponse.success(configService.deleteConfig(id));
    }
}
