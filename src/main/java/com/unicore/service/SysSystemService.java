package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysSystem;
import java.util.List;

public interface SysSystemService extends IService<SysSystem> {
    Page<SysSystem> selectSystemPage(Page<SysSystem> page, SysSystem system);
    List<SysSystem> selectSystemTree();
    List<SysSystem> selectSystemList();
    
    /**
     * 获取用户有权限的系统列表
     * @param userId 用户ID
     * @return 系统列表
     */
    List<SysSystem> selectSystemListByUserId(Integer userId);

    /**
     * 删除应用（校验是否被菜单引用）
     */
    boolean deleteSystem(Integer sysId);
}
