package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unicore.common.WrapperResponse;
import com.unicore.entity.SysUser;
import com.unicore.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @GetMapping("/page")
    public WrapperResponse<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysUser user) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(userService.selectUserPage(page, user));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysUser> getById(@PathVariable Integer id) {
        return WrapperResponse.success(userService.getById(id));
    }

    @PostMapping
    public WrapperResponse<Boolean> add(@RequestBody SysUser user) {
        return WrapperResponse.success(userService.addUser(user));
    }

    @PutMapping
    public WrapperResponse<Boolean> update(@RequestBody SysUser user) {
        return WrapperResponse.success(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Integer id) {
        return WrapperResponse.success(userService.deleteUser(id));
    }

    @PutMapping("/resetPwd")
    public WrapperResponse<Boolean> resetPassword(@RequestBody SysUser user) {
        // 默认重置密码
        String defaultPassword = "asdfg@1234";
        String password = (user.getPassword() != null && !user.getPassword().isEmpty()) 
            ? user.getPassword() : defaultPassword;
        return WrapperResponse.success(userService.resetPassword(user.getUserId(), password));
    }
}
