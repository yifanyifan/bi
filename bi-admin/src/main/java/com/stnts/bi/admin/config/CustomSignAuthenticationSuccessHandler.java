package com.stnts.bi.admin.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证成功处理器
 * @author liutianyuan
 */
public class CustomSignAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    public CustomSignAuthenticationSuccessHandler(String url) {
        this.url = url;
    }

    private String url;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        httpServletResponse.sendRedirect(url);
    }

}