package com.foco.cloud.core.autoconfigure;

import com.foco.cloud.core.interceptor.LoginContextInterceptor;
import com.foco.model.constant.WebInterceptorOrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * description:
 *
 * @Author lucoo
 * @Date 2021/6/23 18:16
 */
@Slf4j
@Order(WebInterceptorOrderConstants.CLOUD_INTERCEPTOR)
public class CloudWebMvcConfigurer implements WebMvcConfigurer {
    LoginContextInterceptor loginContextInterceptor;

    public CloudWebMvcConfigurer(LoginContextInterceptor loginContextInterceptor) {
        this.loginContextInterceptor = loginContextInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginContextInterceptor)
                .addPathPatterns("/**");

    }
}