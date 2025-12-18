package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysRole;
import com.unicore.service.SysMenuService;
import com.unicore.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysMenuService menuService;

    @GetMapping("/page")
    public WrapperResponse<Page<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysRole role) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(roleService.selectRolePage(page, role));
    }

    @GetMapping("/list")
    public WrapperResponse<List<SysRole>> list() {
        return WrapperResponse.success(roleService.list());
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysRole> getById(@PathVariable Integer id) {
        SysRole role = roleService.getById(id);
        if (role != null) {
            role.setMenuIds(menuService.selectMenuIdsByRoleId(id));
        }
        return WrapperResponse.success(role);
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysRole role) {
        return WrapperResponse.success(roleService.addRole(role));
    }

    @PostMapping("/update")
    public WrapperResponse<Boolean> update(@RequestBody SysRole role) {
        return WrapperResponse.success(roleService.updateRole(role));
    }

    @GetMapping("/delete/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        return WrapperResponse.success(roleService.deleteRole(id));
    }
}
