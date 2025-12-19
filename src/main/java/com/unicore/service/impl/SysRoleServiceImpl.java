package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysRole;
import com.unicore.entity.SysRoleMenu;
import com.unicore.entity.SysUserRole;
import com.unicore.mapper.SysRoleMapper;
import com.unicore.mapper.SysRoleMenuMapper;
import com.unicore.mapper.SysUserRoleMapper;
import com.unicore.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
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
        if (result && role.getMenuIds() != null && !role.getMenuIds().isEmpty()) {
            batchInsertRoleMenu(role.getRoleId(), role.getMenuIds());
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
            if (!role.getMenuIds().isEmpty()) {
                batchInsertRoleMenu(role.getRoleId(), role.getMenuIds());
            }
        }
        return result;
    }

    /**
     * 批量插入角色菜单关联
     */
    private void batchInsertRoleMenu(Integer roleId, List<String> menuIds) {
        List<SysRoleMenu> list = new ArrayList<>();
        for (String menuId : menuIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            list.add(rm);
        }
        // 使用 MyBatis Plus 的 insertBatchSomeColumn 或逐条插入
        for (SysRoleMenu rm : list) {
            roleMenuMapper.insert(rm);
        }
    }

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public boolean deleteRole(Integer roleId) {
        // 校验角色是否被用户使用
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, roleId);
        Long count = userRoleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该角色已分配给用户，无法删除");
        }
        return removeById(roleId);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Integer userId) {
        return baseMapper.selectRolesByUserId(userId);
    }
}
