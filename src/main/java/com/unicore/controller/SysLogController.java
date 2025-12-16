package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.unicore.common.WrapperResponse;
import com.unicore.entity.SysLog;
import com.unicore.entity.SysLogininfor;
import com.unicore.mapper.SysLogMapper;
import com.unicore.mapper.SysLogininforMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/log")
public class SysLogController {

    @Autowired
    private SysLogMapper logMapper;

    @Autowired
    private SysLogininforMapper logininforMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String repUrl) {
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (repUrl != null) {
            wrapper.like(SysLog::getRepUrl, repUrl);
        }
        wrapper.orderByDesc(SysLog::getCrteTime);
        return WrapperResponse.success(logMapper.selectPage(page, wrapper));
    }

    @GetMapping("/login/page")
    public WrapperResponse<Page<SysLogininfor>> loginPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName) {
        Page<SysLogininfor> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        if (userName != null) {
            wrapper.like(SysLogininfor::getUserName, userName);
        }
        wrapper.orderByDesc(SysLogininfor::getLoginTime);
        return WrapperResponse.success(logininforMapper.selectPage(page, wrapper));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Long id) {
        return WrapperResponse.success(logMapper.deleteById(id) > 0);
    }

    @DeleteMapping("/login/{id}")
    public WrapperResponse<Boolean> deleteLogin(@PathVariable Long id) {
        return WrapperResponse.success(logininforMapper.deleteById(id) > 0);
    }

    @DeleteMapping("/clear")
    public WrapperResponse<Boolean> clearOper(@RequestParam(required = false) Integer days) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            wrapper.lt(SysLog::getCrteTime, java.time.LocalDateTime.now().minusDays(days));
        }
        return WrapperResponse.success(logMapper.delete(wrapper) >= 0);
    }

    @DeleteMapping("/login/clear")
    public WrapperResponse<Boolean> clearLogin(@RequestParam(required = false) Integer days) {
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            wrapper.lt(SysLogininfor::getLoginTime, java.time.LocalDateTime.now().minusDays(days));
        }
        return WrapperResponse.success(logininforMapper.delete(wrapper) >= 0);
    }
}
