package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.common.utils.Base64Utils;
import com.unicore.common.utils.PasswordValidator;
import com.unicore.entity.SysConfig;
import com.unicore.entity.SysUser;
import com.unicore.mapper.SysConfigMapper;
import com.unicore.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysConfigMapper configMapper;

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
        // 从配置表获取默认密码
        String defaultPassword = "Abc@12345678";
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, "default_password");
        SysConfig config = configMapper.selectOne(wrapper);
        if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
            defaultPassword = config.getConfigValue();
        }
        
        String password = (user.getPassword() != null && !user.getPassword().isEmpty()) 
            ? user.getPassword() : defaultPassword;
        
        // 验证密码复杂度
        String decodedPassword = Base64Utils.decode(password);
        String pwdError = PasswordValidator.validate(decodedPassword);
        if (pwdError != null) {
            return WrapperResponse.fail(pwdError, null);
        }
        
        return WrapperResponse.success(userService.resetPassword(user.getUserId(), password));
    }
}
