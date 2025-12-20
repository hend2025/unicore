package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysConfig;

/**
 * 系统配置服务接口
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 分页查询配置
     */
    Page<SysConfig> selectPage(Page<SysConfig> page, String configName, String configKey);

    /**
     * 根据配置Key获取配置
     */
    SysConfig selectByKey(String key);

    /**
     * 新增配置
     */
    boolean addConfig(SysConfig config);

    /**
     * 更新配置
     */
    boolean updateConfig(SysConfig config);

    /**
     * 删除配置
     */
    boolean deleteConfig(Integer id);
}
