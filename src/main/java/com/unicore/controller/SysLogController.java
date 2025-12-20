package com.unicore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysLog;
import com.unicore.entity.SysLogininfor;
import com.unicore.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/log")
public class SysLogController {

    @Autowired
    private SysLogService logService;

    @GetMapping("/page")
    public WrapperResponse<Page<SysLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String repUrl) {
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(logService.selectOperLogPage(page, repUrl));
    }

    @GetMapping("/login/page")
    public WrapperResponse<Page<SysLogininfor>> loginPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName) {
        Page<SysLogininfor> page = new Page<>(pageNum, pageSize);
        return WrapperResponse.success(logService.selectLoginLogPage(page, userName));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Long id) {
        return WrapperResponse.success(logService.deleteOperLog(id));
    }

    @DeleteMapping("/login/{id}")
    public WrapperResponse<Boolean> deleteLogin(@PathVariable Long id) {
        return WrapperResponse.success(logService.deleteLoginLog(id));
    }

    @DeleteMapping("/clear")
    public WrapperResponse<Boolean> clearOper(@RequestParam(required = false) Integer days) {
        return WrapperResponse.success(logService.clearOperLog(days));
    }

    @DeleteMapping("/login/clear")
    public WrapperResponse<Boolean> clearLogin(@RequestParam(required = false) Integer days) {
        return WrapperResponse.success(logService.clearLoginLog(days));
    }
}
