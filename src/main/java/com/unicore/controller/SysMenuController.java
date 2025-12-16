package com.unicore.controller;

import com.unicore.common.WrapperResponse;
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

    @GetMapping("/tree")
    public WrapperResponse<List<SysMenu>> tree(@RequestParam(required = false) Integer sysId) {
        return WrapperResponse.success(menuService.selectMenuTree(sysId));
    }

    @GetMapping("/list")
    public WrapperResponse<List<SysMenu>> list() {
        return WrapperResponse.success(menuService.list());
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysMenu> getById(@PathVariable Long id) {
        return WrapperResponse.success(menuService.getById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysMenu menu) {
        return WrapperResponse.success(menuService.save(menu));
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysMenu menu) {
        return WrapperResponse.success(menuService.updateById(menu));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Long id) {
        // MyBatis-Plus会自动进行逻辑删除
        return WrapperResponse.success(menuService.removeById(id));
    }
}
