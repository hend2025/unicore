package com.unicore.service.impl;

import cn.hsa.hsaf.auth.security.entity.PortalUserDetails;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.common.utils.Base64Utils;
import com.unicore.common.utils.PasswordValidator;
import com.unicore.entity.SysOrg;
import com.unicore.entity.SysUser;
import com.unicore.entity.SysUserRole;
import com.unicore.mapper.SysOrgMapper;
import com.unicore.mapper.SysUserMapper;
import com.unicore.mapper.SysUserRoleMapper;
import com.unicore.common.utils.SecurityUtils;
import com.unicore.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService, UserDetailsService {

    @Autowired
    SysUserRoleMapper userRoleMapper;

    @Autowired
    SysUserMapper userMapper;

    @Autowired
    SysOrgMapper orgMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, username);
        wrapper.eq(SysUser::getStasFlag, "1");
        SysUser sysUser = userMapper.selectOne(wrapper);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        PortalUserDetails user = new PortalUserDetails();
        user.setUactID(Long.toString(sysUser.getUserId()));
        user.setUserAcct(sysUser.getUserName());
        user.setName(sysUser.getRealName());
        user.setAdmDvs(sysUser.getAdmdvsCode());
        user.setOpterNo(sysUser.getSalt());
        user.setPoolAreaCodg(sysUser.getPassword());

        if (!StringUtils.isEmpty(sysUser.getOrgId())) {
            SysOrg orgEntity = orgMapper.selectById(sysUser.getOrgId());
            user.setOrgUntID(ObjectUtil.isNull(orgEntity) ? null : String.valueOf(orgEntity.getOrgId()));
            user.setOrgCodg(Optional.ofNullable(orgEntity).map(SysOrg::getOrgCode).orElse(null));
            user.setOrgName(Optional.ofNullable(orgEntity).map(SysOrg::getOrgName).orElse(null));
            user.setAdmDvs(Optional.ofNullable(orgEntity).map(SysOrg::getAdmdvsCode).orElse(sysUser.getAdmdvsCode()));
        }

        return user;

    }

    @Override
    public Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user) {
        return (Page<SysUser>) baseMapper.selectUserPage(page, user);
    }

    @Override
    @Transactional
    public boolean addUser(SysUser user) {
        // Base64解码用户名和密码
        String decodedUserName = Base64Utils.decode(user.getUserName());
        String decodedPassword = Base64Utils.decode(user.getPassword());
        user.setUserName(decodedUserName);

        // 验证密码复杂度
        String pwdError = PasswordValidator.validate(decodedPassword);
        if (pwdError != null) {
            throw new RuntimeException(pwdError);
        }

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
        if (user == null)
            return false;
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
        // Base64解码密码
        String decodedOldPassword = Base64Utils.decode(oldPassword);
        String decodedNewPassword = Base64Utils.decode(newPassword);

        if (!passwordEncoder.matches(decodedOldPassword + user.getSalt(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        // 验证新密码复杂度
        String pwdError = PasswordValidator.validate(decodedNewPassword);
        if (pwdError != null) {
            throw new RuntimeException(pwdError);
        }
        // 设置新密码
        String salt = IdUtil.fastSimpleUUID().substring(0, 8);
        user.setSalt(salt);
        user.setPassword(SecurityUtils.encryptPassword(decodedNewPassword + salt));
        return updateById(user);
    }

}
