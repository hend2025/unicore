package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysMenu;
import com.unicore.entity.SysSystem;
import com.unicore.entity.SysUser;
import com.unicore.mapper.SysMenuMapper;
import com.unicore.mapper.SysSystemMapper;
import com.unicore.mapper.SysUserMapper;
import com.unicore.service.SysSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysSystemServiceImpl extends ServiceImpl<SysSystemMapper, SysSystem> implements SysSystemService {

    @Autowired
    private SysMenuMapper menuMapper;
    
    @Autowired
    private SysUserMapper userMapper;

    @Override
    public Page<SysSystem> selectSystemPage(Page<SysSystem> page, SysSystem system) {
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSystem::getValiFlag, "1");
        if (system.getSysName() != null) {
            wrapper.like(SysSystem::getSysName, system.getSysName());
        }
        if (system.getStasFlag() != null) {
            wrapper.eq(SysSystem::getStasFlag, system.getStasFlag());
        }
        wrapper.orderByAsc(SysSystem::getOrderNum);
        return page(page, wrapper);
    }

    @Override
    public List<SysSystem> selectSystemTree() {
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSystem::getValiFlag, "1");
        wrapper.orderByAsc(SysSystem::getOrderNum);
        List<SysSystem> systems = list(wrapper);
        return buildTree(systems, 0);
    }

    private List<SysSystem> buildTree(List<SysSystem> systems, Integer parentId) {
        return systems.stream()
            .filter(s -> parentId.equals(s.getPrntId()))
            .peek(s -> s.setChildren(buildTree(systems, s.getSysId())))
            .collect(Collectors.toList());
    }

    @Override
    public List<SysSystem> selectSystemList() {
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSystem::getValiFlag, "1");
        wrapper.eq(SysSystem::getStasFlag, "1");
        wrapper.orderByAsc(SysSystem::getOrderNum);
        return list(wrapper);
    }
    
    @Override
    public List<SysSystem> selectSystemListByUserId(Integer userId) {
        // 判断是否为admin用户
        SysUser user = userMapper.selectById(userId);
        if (user != null && "admin".equals(user.getUserName())) {
            // admin用户返回所有系统
            return selectSystemList();
        }
        
        // 获取用户有权限的菜单
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取不重复的系统ID
        Set<Integer> sysIds = menus.stream()
            .filter(m -> m.getSysId() != null)
            .map(SysMenu::getSysId)
            .collect(Collectors.toSet());
        
        if (sysIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询对应的系统信息
        LambdaQueryWrapper<SysSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSystem::getValiFlag, "1");
        wrapper.eq(SysSystem::getStasFlag, "1");
        wrapper.in(SysSystem::getSysId, sysIds);
        wrapper.orderByAsc(SysSystem::getOrderNum);
        return list(wrapper);
    }

    @Override
    public boolean deleteSystem(Integer sysId) {
        // 校验应用是否被菜单引用
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getSysId, sysId);
        wrapper.eq(SysMenu::getValiFlag, "1");
        Long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该应用下存在菜单，无法删除");
        }
        return removeById(sysId);
    }
}
