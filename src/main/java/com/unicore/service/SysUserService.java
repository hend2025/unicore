package com.unicore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.unicore.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface SysUserService extends IService<SysUser> {
    Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user);
    boolean addUser(SysUser user);
    boolean updateUser(SysUser user);
    boolean deleteUser(Integer userId);
    boolean resetPassword(Integer userId, String password);
    boolean changePassword(Integer userId, String oldPassword, String newPassword);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
