package com.unicore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unicore.entity.SysMenu;
import com.unicore.mapper.SysMenuMapper;
import com.unicore.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unicore.entity.SysUser;
import com.unicore.mapper.SysUserMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private static final int MENU_ID_LENGTH = 19;
    private static final int SYS_ID_LENGTH = 3;
    private static final int NODE_LENGTH = 2;

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
    public IPage<SysMenu> selectMenuPage(IPage<SysMenu> page, SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getValiFlag, "1");
        if (menu.getSysId() != null) {
            wrapper.eq(SysMenu::getSysId, menu.getSysId());
        }
        if (StringUtils.hasText(menu.getMenuName())) {
            wrapper.like(SysMenu::getMenuName, menu.getMenuName());
        }
        if (StringUtils.hasText(menu.getMenuUrl())) {
            wrapper.like(SysMenu::getMenuUrl, menu.getMenuUrl());
        }
        if (StringUtils.hasText(menu.getStasFlag())) {
            wrapper.eq(SysMenu::getStasFlag, menu.getStasFlag());
        }
        wrapper.orderByAsc(SysMenu::getSysId, SysMenu::getOrderNum);
        return page(page, wrapper);
    }

    @Override
    public String generateMenuId(Integer sysId, String parentId) {
        if (sysId == null) {
            throw new IllegalArgumentException("系统ID不能为空");
        }
        
        // 系统ID格式化为3位
        String sysIdStr = String.format("%0" + SYS_ID_LENGTH + "d", sysId);
        
        // 如果是根节点（parentId为空或为"0"）
        if (!StringUtils.hasText(parentId) || "0".equals(parentId)) {
            // 查询该系统下一级菜单的最大ID
            String maxMenuId = baseMapper.selectMaxMenuId(sysId, "0");
            int nextSeq = 1;
            if (StringUtils.hasText(maxMenuId) && maxMenuId.length() >= SYS_ID_LENGTH + NODE_LENGTH) {
                // 提取第一个节点的序号（第4-5位）
                String seqStr = maxMenuId.substring(SYS_ID_LENGTH, SYS_ID_LENGTH + NODE_LENGTH);
                nextSeq = Integer.parseInt(seqStr) + 1;
            }
            // 生成新ID：系统ID(3位) + 节点序号(2位) + 补0
            String nodeStr = String.format("%0" + NODE_LENGTH + "d", nextSeq);
            return padRight(sysIdStr + nodeStr, MENU_ID_LENGTH);
        }
        
        // 非根节点，基于父节点ID生成
        // 找到父节点ID中最后一个非0的节点位置
        int level = getMenuLevel(parentId);
        int nextNodeStart = SYS_ID_LENGTH + level * NODE_LENGTH;
        
        if (nextNodeStart + NODE_LENGTH > MENU_ID_LENGTH) {
            throw new IllegalArgumentException("菜单层级已达到最大限制");
        }
        
        // 查询同级菜单的最大ID
        String maxMenuId = baseMapper.selectMaxMenuId(sysId, parentId);
        int nextSeq = 1;
        if (StringUtils.hasText(maxMenuId) && maxMenuId.length() > nextNodeStart) {
            // 提取下一级节点的序号
            String seqStr = maxMenuId.substring(nextNodeStart, Math.min(nextNodeStart + NODE_LENGTH, maxMenuId.length()));
            if (seqStr.matches("\\d+")) {
                nextSeq = Integer.parseInt(seqStr) + 1;
            }
        }
        
        // 生成新ID：父节点前缀 + 新节点序号 + 补0
        String prefix = parentId.substring(0, nextNodeStart);
        String nodeStr = String.format("%0" + NODE_LENGTH + "d", nextSeq);
        return padRight(prefix + nodeStr, MENU_ID_LENGTH);
    }

    /**
     * 获取菜单层级（从1开始）
     */
    private int getMenuLevel(String menuId) {
        if (!StringUtils.hasText(menuId) || "0".equals(menuId)) {
            return 0;
        }
        // 去掉系统ID前缀后，每2位代表一个层级
        String nodesPart = menuId.length() > SYS_ID_LENGTH ? menuId.substring(SYS_ID_LENGTH) : "";
        int level = 0;
        for (int i = 0; i < nodesPart.length(); i += NODE_LENGTH) {
            String node = nodesPart.substring(i, Math.min(i + NODE_LENGTH, nodesPart.length()));
            if (node.matches("0+")) {
                break;
            }
            level++;
        }
        return level;
    }

    /**
     * 右侧补0到指定长度
     */
    private String padRight(String str, int length) {
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append("0");
        }
        return sb.toString();
    }

    @Override
    public boolean saveMenu(SysMenu menu) {
        // 自动生成菜单ID
        String menuId = generateMenuId(menu.getSysId(), menu.getParentId());
        menu.setMenuId(menuId);
        return save(menu);
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
        return buildTree(menus, "0");
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Integer userId) {
        return selectMenuTreeByUserId(userId, null);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Integer userId, Long sysId) {
        List<SysMenu> menus;
        if (isAdminUser(userId)) {
            menus = selectAllMenus(sysId);
        } else {
            menus = baseMapper.selectMenusByUserId(userId);
            if (menus == null || menus.isEmpty()) {
                return new ArrayList<>();
            }
            if (sysId != null) {
                menus = menus.stream()
                    .filter(m -> m.getSysId() != null && m.getSysId().equals(sysId.intValue()))
                    .collect(Collectors.toList());
            }
        }
        return buildMenuTree(menus);
    }

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
            String parentId = menu.getParentId();
            if (!StringUtils.hasText(parentId) || "0".equals(parentId)) {
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
    
    private List<SysMenu> getChildren(String parentId, List<SysMenu> allMenus) {
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
    public List<String> selectMenuIdsByRoleId(Integer roleId) {
        return baseMapper.selectMenuIdsByRoleId(roleId);
    }

    private List<SysMenu> buildTree(List<SysMenu> menus, String parentId) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysMenu> result = new ArrayList<>();
        String pid = StringUtils.hasText(parentId) ? parentId : "0";
        for (SysMenu menu : menus) {
            if (menu == null) continue;
            String menuParentId = menu.getParentId();
            String mpid = StringUtils.hasText(menuParentId) ? menuParentId : "0";
            if (pid.equals(mpid)) {
                String menuId = menu.getMenuId();
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
