package com.unicore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysMenu;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> selectMenuTree(Integer sysId);
    List<SysMenu> selectMenuTreeByUserId(Integer userId);
    List<SysMenu> selectMenuTreeByUserId(Integer userId, Long sysId);
    List<String> selectMenuIdsByRoleId(Integer roleId);
    
    /**
     * 分页查询菜单列表
     */
    IPage<SysMenu> selectMenuPage(IPage<SysMenu> page, SysMenu menu);
    
    /**
     * 生成菜单ID
     * @param sysId 系统ID
     * @param parentId 父节点ID
     * @return 新的菜单ID
     */
    String generateMenuId(Integer sysId, String parentId);
    
    /**
     * 新增菜单（自动生成ID）
     */
    boolean saveMenu(SysMenu menu);
}
