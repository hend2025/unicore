package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysLog;
import com.unicore.entity.SysLogininfor;
import com.unicore.mapper.SysLogMapper;
import com.unicore.mapper.SysLogininforMapper;
import com.unicore.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 日志管理服务实现类
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Autowired
    private SysLogininforMapper logininforMapper;

    @Override
    public Page<SysLog> selectOperLogPage(Page<SysLog> page, String repUrl) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(repUrl)) {
            wrapper.like(SysLog::getRepUrl, repUrl);
        }
        wrapper.orderByDesc(SysLog::getCrteTime);
        return page(page, wrapper);
    }

    @Override
    public Page<SysLogininfor> selectLoginLogPage(Page<SysLogininfor> page, String userName) {
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(userName)) {
            wrapper.like(SysLogininfor::getUserName, userName);
        }
        wrapper.orderByDesc(SysLogininfor::getLoginTime);
        return logininforMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean deleteOperLog(Long id) {
        return removeById(id);
    }

    @Override
    public boolean deleteLoginLog(Long id) {
        return logininforMapper.deleteById(id) > 0;
    }

    @Override
    public boolean clearOperLog(Integer days) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            wrapper.lt(SysLog::getCrteTime, LocalDateTime.now().minusDays(days));
        }
        return remove(wrapper);
    }

    @Override
    public boolean clearLoginLog(Integer days) {
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        if (days != null && days > 0) {
            wrapper.lt(SysLogininfor::getLoginTime, LocalDateTime.now().minusDays(days));
        }
        return logininforMapper.delete(wrapper) >= 0;
    }
}
