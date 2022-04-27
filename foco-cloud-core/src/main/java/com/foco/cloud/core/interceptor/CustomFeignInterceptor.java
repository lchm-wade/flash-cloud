package com.foco.cloud.core.interceptor;


import com.alibaba.fastjson.JSON;
import com.foco.context.core.*;
import com.foco.model.page.PageParam;
import com.foco.context.util.HttpContext;
import com.foco.model.constant.FocoConstants;
import com.foco.model.page.ThreadPagingUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
/**
 * description: 处理请求上下文传递
 *
 * @Author lucoo
 * @Date 2021/6/2 11:48
 */
@Slf4j
public class CustomFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        buildHead(requestTemplate);
        String headers = JSON.toJSONString(HttpContext.getHeaders());
        if (!"{}".equals(headers)) {
            requestTemplate.header(FocoConstants.HTTP_CONTEXT, headers);
        }
    }

    private void buildHead(RequestTemplate requestTemplate) {
        //传递loginUser参数
        PageParam pageParam = ThreadPagingUtil.get();
        String pageValue = null;
        if (pageParam != null) {
            pageValue = JSON.toJSONString(pageParam);
        }
        String loginContext = LoginContextHolder.get(true);
        if(!"{}".equals(loginContext)){
            requestTemplate
                    .header(LoginContextConstant.LOGIN_CONTEXT, loginContext);
        }
        FocoContextManager.setHeader((header,focoContext)->{
            if(!"{}".equals(focoContext)){
                requestTemplate.header(header,focoContext);
            }
        });
        requestTemplate
                .header(FocoConstants.ORIGINAL, FocoConstants.FEIGN_ORIGINAL)
                .header(FocoConstants.PAGE, pageValue);
    }

}
