package com.foco.cloud.core.autoconfigure;

import com.foco.cloud.core.hystrix.FeignHystrixConcurrencyStrategy;
import com.foco.cloud.core.hystrix.FeignHystrixErrorDecoder;
import com.foco.cloud.core.hystrix.FeignHystrixExceptionHandler;
import com.foco.model.constant.FocoConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/10/28 14:08
 */
@ConditionalOnClass(name = "com.netflix.hystrix.Hystrix")
@ConditionalOnProperty(prefix = "feign.hystrix", name = FocoConstants.ENABLED)
public class HystrixAutoConfiguration {
    /**
     * 自定义Hystrix的并发策略，解决上下文对象的ThreadLocal 传递问题
     */
    @Bean
    public FeignHystrixConcurrencyStrategy createFeignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }
    /**
     * 自定义hystrix Feign 错误解码器
     */
    @Bean
    public FeignHystrixErrorDecoder feignHystrixErrorDecoder() {
        return new FeignHystrixErrorDecoder();
    }
    @Bean
    FeignHystrixExceptionHandler feignHystrixExceptionHandler(){
        return new FeignHystrixExceptionHandler();
    }
}
