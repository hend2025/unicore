package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysLog;
import com.unicore.entity.SysLogininfor;

/**
 * 日志管理服务接口
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 分页查询操作日志
     */
    Page<SysLog> selectOperLogPage(Page<SysLog> page, String repUrl);

    /**
     * 分页查询登录日志
     */
    Page<SysLogininfor> selectLoginLogPage(Page<SysLogininfor> page, String userName);

    /**
     * 删除操作日志
     */
    boolean deleteOperLog(Long id);

    /**
     * 删除登录日志
     */
    boolean deleteLoginLog(Long id);

    /**
     * 清理操作日志
     */
    boolean clearOperLog(Integer days);

    /**
     * 清理登录日志
     */
    boolean clearLoginLog(Integer days);
}
