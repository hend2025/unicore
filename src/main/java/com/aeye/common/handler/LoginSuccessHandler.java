package com.aeye.common.handler;

import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public LoginSuccessHandler() {}

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String json = "{\n" +
                "        \"accessToken\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9\",\n" +
                "        \"tokenType\": \"Bearer\",\n" +
                "        \"refreshToken\": null,\n" +
                "        \"expires\": null\n" +
                "    }";
        WrapperResponse wrapperResponse = WrapperResponse.success("登录成功!", JSON.parse(json));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JSON.toJSONString(wrapperResponse));

    }

}
