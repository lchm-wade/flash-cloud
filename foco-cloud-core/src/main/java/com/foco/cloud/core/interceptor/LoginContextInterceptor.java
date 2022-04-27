package com.foco.cloud.core.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.foco.context.core.*;
import com.foco.context.util.HttpContext;
import com.foco.model.constant.FocoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 微服务登录态传递
 *
 * @Author lucoo
 * @Date 2021/6/26 14:55
 */
@Slf4j
public class LoginContextInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request != null) {
            String loginContextStr = request.getHeader(LoginContextConstant.LOGIN_CONTEXT);
            if (StrUtil.isNotBlank(loginContextStr)) {
                LoginContextHolder.set(loginContextStr);
            }
            FocoContextManager.setLocal((focoContextHeader)-> request.getHeader(focoContextHeader));
            String headerMap = request.getHeader(FocoConstants.HTTP_CONTEXT);
            if(StrUtil.isNotBlank(headerMap)){
                HttpContext.setHeaders(JSON.parseObject(headerMap, Map.class));
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginContextHolder.remove();
        HttpContext.cleanHeaders();
        FocoContextManager.remove();
    }

}