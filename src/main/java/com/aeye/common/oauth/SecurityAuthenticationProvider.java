package com.aeye.common.oauth;

import java.util.Collection;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unicore.common.Base64Utils;
import com.unicore.entity.SysUser;
import com.unicore.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import cn.hsa.hsaf.auth.security.entity.PortalUserDetails;


@Service
public class SecurityAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SysUserService userService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Base64解码用户名
        String userName = Base64Utils.decode(authentication.getName());
        
        // 直接查询用户获取密码和盐值
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, userName);
        wrapper.eq(SysUser::getValiFlag, "1");
        SysUser sysUser = userService.getOne(wrapper);
        if (sysUser == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        // Base64解码密码并验证
        String password = (String) authentication.getCredentials();
        String decodedPassword = Base64Utils.decode(password);
        // 密码加盐后验证（数据库存储的是 BCrypt(password + salt)）
        String passwordWithSalt = decodedPassword + sysUser.getSalt();
        if (!passwordEncoder.matches(passwordWithSalt, sysUser.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 加载用户详情（包含权限）
        PortalUserDetails entity = (PortalUserDetails) userService.loadUserByUsername(userName);
        
        // 构建返回的用户登录成功的token
        Collection<? extends GrantedAuthority> authorities = entity.getAuthorities();
        return new UsernamePasswordAuthenticationToken(entity, password, authorities);
    }

    /**
     * 执行支持判断
     *
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // 返回 true ，表示支持执行
        return true;
    }

}
