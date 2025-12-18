package com.unicore.common.handler;

import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnicoreAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public UnicoreAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        WrapperResponse<String> result = WrapperResponse.fail("登录失败！"+exception.getMessage(), null);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JSON.toJSONString(result));
    }

}
