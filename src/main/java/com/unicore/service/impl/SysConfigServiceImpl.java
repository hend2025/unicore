package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysConfig;
import com.unicore.mapper.SysConfigMapper;
import com.unicore.service.SysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统配置服务实现类
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public Page<SysConfig> selectPage(Page<SysConfig> page, String configName, String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getValiFlag, "1");
        if (StringUtils.hasText(configName)) {
            wrapper.like(SysConfig::getConfigName, configName);
        }
        if (StringUtils.hasText(configKey)) {
            wrapper.like(SysConfig::getConfigKey, configKey);
        }
        return page(page, wrapper);
    }

    @Override
    public SysConfig selectByKey(String key) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        wrapper.eq(SysConfig::getValiFlag, "1");
        return getOne(wrapper);
    }

    @Override
    public boolean addConfig(SysConfig config) {
        return save(config);
    }

    @Override
    public boolean updateConfig(SysConfig config) {
        return updateById(config);
    }

    @Override
    public boolean deleteConfig(Integer id) {
        return removeById(id);
    }
}
