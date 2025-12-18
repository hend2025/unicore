package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysSystem;
import com.unicore.service.SysSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys/system")
public class SysSystemController {

    @Autowired
    private SysSystemService systemService;

    @GetMapping("/page")
    public WrapperResponse<Page<SysSystem>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysSystem system) {
        Page<SysSystem> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(systemService.selectSystemPage(page, system));
    }

    @GetMapping("/tree")
    public WrapperResponse<List<SysSystem>> tree() {
        return WrapperResponse.success(systemService.selectSystemTree());
    }

    @GetMapping("/list")
    public WrapperResponse<List<SysSystem>> list() {
        return WrapperResponse.success(systemService.selectSystemList());
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysSystem> getById(@PathVariable Integer id) {
        return WrapperResponse.success(systemService.getById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysSystem system) {
        return WrapperResponse.success(systemService.save(system));
    }

    @PostMapping("/update")
    public WrapperResponse<Boolean> update(@RequestBody SysSystem system) {
        return WrapperResponse.success(systemService.updateById(system));
    }

    @GetMapping("/delete/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(systemService.removeById(id));
    }
}
