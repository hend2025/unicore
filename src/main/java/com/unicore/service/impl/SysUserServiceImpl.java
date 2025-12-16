package com.unicore.service.impl;

import cn.hsa.hsaf.auth.security.entity.PortalUserDetails;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.common.Base64Utils;
import com.unicore.entity.SysUser;
import com.unicore.entity.SysUserRole;
import com.unicore.mapper.SysUserMapper;
import com.unicore.mapper.SysUserRoleMapper;
import com.unicore.security.SecurityUtils;
import com.unicore.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService, UserDetailsService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        wrapper.eq(SysUser::getValiFlag, "1");
        SysUser sysUser = userMapper.selectOne(wrapper);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        Set<String> permissions = new HashSet<>();
        // admin用户拥有所有权限
        if ("admin".equals(username)) {
            permissions.add("*:*:*");
        } else {
            List<String> perms = userMapper.selectPermsByUserId(sysUser.getUserId());
            if (perms != null) {
                permissions.addAll(perms);
            }
        }

        PortalUserDetails user = new PortalUserDetails();
        user.setUactID(Long.toString(sysUser.getUserId()));
        user.setUserAcct(sysUser.getUserName());
        user.setName(sysUser.getRealName());
//        user.setSalt(sysUser.getSalt());
//        user.setPwd(sysUser.getPassword());
        return user;

    }

    @Override
    public Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user) {
        List<SysUser> list = baseMapper.selectUserList(user);
        long total = list.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = Math.min(start + (int) page.getSize(), list.size());
        if (start >= list.size()) {
            page.setRecords(java.util.Collections.emptyList());
        } else {
            page.setRecords(list.subList(start, end));
        }
        page.setTotal(total);
        return page;
    }

    @Override
    @Transactional
    public boolean addUser(SysUser user) {
        // Base64解码用户名和密码
        String decodedUserName = Base64Utils.decode(user.getUserName());
        String decodedPassword = Base64Utils.decode(user.getPassword());
        user.setUserName(decodedUserName);
        
        // 生成盐值并加密密码
        String salt = IdUtil.fastSimpleUUID().substring(0, 8);
        user.setSalt(salt);
        user.setPassword(SecurityUtils.encryptPassword(decodedPassword + salt));
        boolean result = save(user);
        if (result && user.getRoleIds() != null) {
            for (Integer roleId : user.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public boolean updateUser(SysUser user) {
        // Base64解码用户名
        String decodedUserName = Base64Utils.decode(user.getUserName());
        user.setUserName(decodedUserName);
        
        // 检查是否为admin用户
        SysUser existUser = getById(user.getUserId());
        if (existUser != null && "admin".equals(existUser.getUserName())) {
            // admin用户不能被禁用
            if (user.getStasFlag() != null && !"1".equals(user.getStasFlag())) {
                throw new RuntimeException("admin用户不能被禁用");
            }
        }
        user.setPassword(null);
        user.setSalt(null);
        boolean result = updateById(user);
        if (result && user.getRoleIds() != null) {
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getUserId, user.getUserId());
            userRoleMapper.delete(wrapper);
            for (Integer roleId : user.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
        return result;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        // 检查是否为admin用户
        SysUser user = getById(userId);
        if (user != null && "admin".equals(user.getUserName())) {
            throw new RuntimeException("admin用户不能被删除");
        }
        // MyBatis-Plus会自动进行逻辑删除（因为SysUser继承了BaseEntity，有@TableLogic注解）
        return removeById(userId);
    }

    @Override
    public boolean resetPassword(Integer userId, String password) {
        SysUser user = getById(userId);
        if (user == null) return false;
        // Base64解码密码
        String decodedPassword = Base64Utils.decode(password);
        String salt = IdUtil.fastSimpleUUID().substring(0, 8);
        user.setSalt(salt);
        user.setPassword(SecurityUtils.encryptPassword(decodedPassword + salt));
        return updateById(user);
    }

    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证原密码 - 使用BCrypt的matches方法验证
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword + user.getSalt(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        // 设置新密码
        String salt = IdUtil.fastSimpleUUID().substring(0, 8);
        user.setSalt(salt);
        user.setPassword(SecurityUtils.encryptPassword(newPassword + salt));
        return updateById(user);
    }

}
