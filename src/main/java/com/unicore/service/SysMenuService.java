package com.unicore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysMenu;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> selectMenuTree(Integer sysId);
    List<SysMenu> selectMenuTreeByUserId(Integer userId);
    List<SysMenu> selectMenuTreeByUserId(Integer userId, Long sysId);
    List<Long> selectMenuIdsByRoleId(Integer roleId);
}
