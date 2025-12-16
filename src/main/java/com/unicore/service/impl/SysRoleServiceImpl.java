package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysRole;
import com.unicore.entity.SysRoleMenu;
import com.unicore.mapper.SysRoleMapper;
import com.unicore.mapper.SysRoleMenuMapper;
import com.unicore.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public Page<SysRole> selectRolePage(Page<SysRole> page, SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getValiFlag, "1");
        if (role.getRoleName() != null) {
            wrapper.like(SysRole::getRoleName, role.getRoleName());
        }
        if (role.getRoleKey() != null) {
            wrapper.like(SysRole::getRoleKey, role.getRoleKey());
        }
        if (role.getStasFlag() != null) {
            wrapper.eq(SysRole::getStasFlag, role.getStasFlag());
        }
        wrapper.orderByDesc(SysRole::getCrteTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public boolean addRole(SysRole role) {
        boolean result = save(role);
        if (result && role.getMenuIds() != null) {
            for (Long menuId : role.getMenuIds()) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getRoleId());
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public boolean updateRole(SysRole role) {
        boolean result = updateById(role);
        if (result && role.getMenuIds() != null) {
            LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRoleMenu::getRoleId, role.getRoleId());
            roleMenuMapper.delete(wrapper);
            for (Long menuId : role.getMenuIds()) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getRoleId());
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
        return result;
    }

    @Override
    public boolean deleteRole(Integer roleId) {
        // MyBatis-Plus会自动进行逻辑删除（因为SysRole继承了BaseEntity，有@TableLogic注解）
        return removeById(roleId);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Integer userId) {
        return baseMapper.selectRolesByUserId(userId);
    }
}
