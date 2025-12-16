package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysMenu;
import com.unicore.mapper.SysMenuMapper;
import com.unicore.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unicore.entity.SysUser;
import com.unicore.mapper.SysUserMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 判断是否为admin用户
     */
    private boolean isAdminUser(Integer userId) {
        if (userId == null) return false;
        SysUser user = userMapper.selectById(userId);
        return user != null && "admin".equals(user.getUserName());
    }

    @Override
    public List<SysMenu> selectMenuTree(Integer sysId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getValiFlag, "1");
        if (sysId != null) {
            wrapper.eq(SysMenu::getSysId, sysId);
        }
        wrapper.orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> menus = list(wrapper);
        return buildTree(menus, 0L);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Integer userId) {
        return selectMenuTreeByUserId(userId, null);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Integer userId, Long sysId) {
        List<SysMenu> menus;
        // admin用户拥有所有菜单权限（通过用户名判断）
        if (isAdminUser(userId)) {
            menus = selectAllMenus(sysId);
        } else {
            menus = baseMapper.selectMenusByUserId(userId);
            if (menus == null || menus.isEmpty()) {
                return new ArrayList<>();
            }
            // 如果指定了sysId，过滤菜单
            if (sysId != null) {
                menus = menus.stream()
                    .filter(m -> m.getSysId() != null && m.getSysId().equals(sysId.intValue()))
                    .collect(Collectors.toList());
            }
        }
        return buildMenuTree(menus);
    }

    /**
     * 获取所有菜单（admin用户专用）
     */
    private List<SysMenu> selectAllMenus(Long sysId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getValiFlag, "1");
        wrapper.eq(SysMenu::getStasFlag, "1");
        wrapper.in(SysMenu::getMenuType, "1", "2");
        wrapper.eq(SysMenu::getIsShow, "1");
        if (sysId != null) {
            wrapper.eq(SysMenu::getSysId, sysId.intValue());
        }
        wrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        return list(wrapper);
    }
    
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> rootMenus = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu == null) continue;
            Long parentId = menu.getParentId();
            if (parentId == null || parentId.longValue() == 0L) {
                rootMenus.add(menu);
            }
        }
        for (SysMenu root : rootMenus) {
            if (root != null && root.getMenuId() != null) {
                root.setChildren(getChildren(root.getMenuId(), menus));
            }
        }
        return rootMenus;
    }
    
    private List<SysMenu> getChildren(Long parentId, List<SysMenu> allMenus) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (parentId != null && parentId.equals(menu.getParentId())) {
                menu.setChildren(getChildren(menu.getMenuId(), allMenus));
                children.add(menu);
            }
        }
        return children;
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Integer roleId) {
        return baseMapper.selectMenuIdsByRoleId(roleId);
    }

    private List<SysMenu> buildTree(List<SysMenu> menus, Long parentId) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysMenu> result = new ArrayList<>();
        long pid = parentId == null ? 0L : parentId;
        for (SysMenu menu : menus) {
            if (menu == null) continue;
            Long menuParentId = menu.getParentId();
            long mpid = menuParentId == null ? 0L : menuParentId;
            if (mpid == pid) {
                Long menuId = menu.getMenuId();
                if (menuId != null) {
                    menu.setChildren(buildTree(menus, menuId));
                } else {
                    menu.setChildren(new ArrayList<>());
                }
                result.add(menu);
            }
        }
        return result;
    }
}
