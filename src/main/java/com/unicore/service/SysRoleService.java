package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysRole;
import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    Page<SysRole> selectRolePage(Page<SysRole> page, SysRole role);
    boolean addRole(SysRole role);
    boolean updateRole(SysRole role);
    boolean deleteRole(Integer roleId);
    List<SysRole> selectRolesByUserId(Integer userId);
}
