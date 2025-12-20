package com.unicore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.entity.SysApiLog;
import com.unicore.mapper.SysApiLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys/apilog")
public class SysApiLogController {

    @Autowired
    private SysApiLogMapper apiLogMapper;

    @GetMapping("/page")
    public WrapperResponse<Page<SysApiLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String repUrl,
            @RequestParam(required = false) String repMethod,
            @RequestParam(required = false) String execStat) {
        Page<SysApiLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysApiLog> wrapper = new LambdaQueryWrapper<>();
        if (repUrl != null && !repUrl.isEmpty()) {
            wrapper.like(SysApiLog::getRepUrl, repUrl);
        }
        if (repMethod != null && !repMethod.isEmpty()) {
            wrapper.eq(SysApiLog::getRepMethod, repMethod);
        }
        if (execStat != null && !execStat.isEmpty()) {
            wrapper.eq(SysApiLog::getExecStat, execStat);
        }
        wrapper.orderByDesc(SysApiLog::getCrteTime);
        return WrapperResponse.success(apiLogMapper.selectPage(page, wrapper));
    }

    @GetMapping("/{id}")
    public WrapperResponse<SysApiLog> getById(@PathVariable Long id) {
        return WrapperResponse.success(apiLogMapper.selectById(id));
    }

    @DeleteMapping("/{id}")
    public WrapperResponse<Boolean> delete(@PathVariable Long id) {
        // API日志表没有vali_flag字段，进行物理删除
        return WrapperResponse.success(apiLogMapper.deleteById(id) > 0);
    }

    @PostMapping("/batch/delete")
    public WrapperResponse<Boolean> deleteBatch(@RequestBody Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += apiLogMapper.deleteById(id);
        }
        return WrapperResponse.success(count > 0);
    }

    @DeleteMapping("/clear")
    public WrapperResponse<Boolean> clear(@RequestParam(required = false) Integer days) {
        LambdaQueryWrapper<SysApiLog> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            // 删除N天前的日志
            wrapper.apply("DATE(CRTE_TIME) < DATE_SUB(CURDATE(), INTERVAL {0} DAY)", days);
        }
        return WrapperResponse.success(apiLogMapper.delete(wrapper) > 0);
    }
}
