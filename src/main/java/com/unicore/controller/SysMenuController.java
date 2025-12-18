package com.unicore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysMenu;
import com.unicore.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @GetMapping("/page")
    public WrapperResponse<IPage<SysMenu>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysMenu menu) {
        Page<SysMenu> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(menuService.selectMenuPage(page, menu));
    }

    @GetMapping("/tree")
    public WrapperResponse<List<SysMenu>> tree(@RequestParam(required = false) Integer sysId) {
        return WrapperResponse.success(menuService.selectMenuTree(sysId));
    }

    @GetMapping("/list")
    public WrapperResponse<List<SysMenu>> list() {
        return WrapperResponse.success(menuService.list());
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysMenu> getById(@PathVariable String id) {
        return WrapperResponse.success(menuService.getById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysMenu menu) {
        return WrapperResponse.success(menuService.saveMenu(menu));
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysMenu menu) {
        return WrapperResponse.success(menuService.updateById(menu));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable String id) {
        return WrapperResponse.success(menuService.removeById(id));
    }
}
