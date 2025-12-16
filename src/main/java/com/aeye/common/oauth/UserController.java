package com.aeye.common.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;

@RestController
public class UserController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    // 获取当前用户信息
    @GetMapping("/user/me")
    public Object getUser(Authentication authentication) {
        return authentication;
    }

    // 退出登录，请清除redis里面的token
    @GetMapping(value = "/user/forceLogout")
    public CodeMessage forceLogout(String accessToken) {
        try{
            consumerTokenServices.revokeToken(accessToken);
        }catch (Exception e){
            return CodeMessage.ERROR;
        }
        return CodeMessage.SUCCESS;
    }

    @ResponseBody
    @RequestMapping("/user/getUser")
    public WrapperResponse getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String json = JSON.toJSONString(authentication.getPrincipal());
        return WrapperResponse.success(JSON.parse(json));
    }

}
