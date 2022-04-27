package com.foco.cloud.core.autoconfigure;


import com.foco.cloud.core.feign.FeignErrorDecoder;
import com.foco.cloud.core.feign.FeignExceptionHandler;
import com.foco.cloud.core.interceptor.CustomFeignInterceptor;
import com.foco.context.core.IContext;
import com.foco.model.constant.FocoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description: 处理熔断器线程池模式下，请求上下文线程间传递
 *
 * @Author lucoo
 * @Date 2021/6/2 9:57
 **/
@Slf4j
@Configuration
public class FeignAutoConfiguration {
    /**
     * 自定义Feign请求拦截器
     */
    @Bean
    public CustomFeignInterceptor loginContextFeignInterceptor() {
        return new CustomFeignInterceptor();
    }
    /**
     * 自定义Feign 错误解码器
     */
    @Bean
    @ConditionalOnProperty(prefix = "feign.hystrix", name = FocoConstants.ENABLED,havingValue = "false",matchIfMissing = true)
    public FeignErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    FeignExceptionHandler feignExceptionHandler(){
        return new FeignExceptionHandler();
    }
}
