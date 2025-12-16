package com.unicore.controller;

import cn.hsa.hsaf.auth.security.entity.PortalUserDetails;
import com.unicore.common.WrapperResponse;
import com.unicore.entity.SysMenu;
import com.unicore.service.SysMenuService;
import com.unicore.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysUserService userService;

    @GetMapping("/info")
    public WrapperResponse<Map<String, Object>> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof PortalUserDetails)) {
            return WrapperResponse.error(401, "未登录或会话已过期，请重新登录");
        }
        PortalUserDetails loginUser = (PortalUserDetails) principal;
        Map<String, Object> result = new HashMap<>();
        result.put("userId", loginUser.getUactID());
        result.put("userName", loginUser.getUsername());
        result.put("realName", loginUser.getName());
//        result.put("permissions", loginUser.getp);
        return WrapperResponse.success(result);
    }

    @GetMapping("/menus")
    public WrapperResponse<List<SysMenu>> getMenus(@RequestParam(required = false) Long sysId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof PortalUserDetails)) {
            return WrapperResponse.error(401, "未登录或会话已过期，请重新登录");
        }
        PortalUserDetails loginUser = (PortalUserDetails) principal;
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(Integer.parseInt(loginUser.getUactID()), sysId);
        return WrapperResponse.success(menus);
    }

    @PutMapping("/password")
    public WrapperResponse<Void> changePassword(@RequestBody Map<String, String> params) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof PortalUserDetails)) {
            return WrapperResponse.error(401, "未登录或会话已过期，请重新登录");
        }
        PortalUserDetails loginUser = (PortalUserDetails) principal;
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return WrapperResponse.error(400, "参数不完整");
        }
        try {
            userService.changePassword(Integer.parseInt(loginUser.getUactID()), oldPassword, newPassword);
            return WrapperResponse.success();
        } catch (RuntimeException e) {
            return WrapperResponse.error(400, e.getMessage());
        }
    }

}
