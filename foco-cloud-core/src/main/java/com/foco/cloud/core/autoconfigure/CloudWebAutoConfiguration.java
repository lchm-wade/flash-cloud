package com.foco.cloud.core.autoconfigure;

import com.foco.cloud.core.executor.CloudThreadLocalExecutor;
import com.foco.cloud.core.interceptor.LoginContextInterceptor;
import com.foco.context.executor.ThreadLocalExecutor;
import com.foco.model.constant.MainClassConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/12/31 14:36
 * @since foco2.1.0
 */
@ConditionalOnClass(name=MainClassConstant.SPRING_WEB_MVC)
public class CloudWebAutoConfiguration {
    @Bean
    LoginContextInterceptor loginContextInterceptor(){
        return new LoginContextInterceptor();
    }
    @Bean
    CloudWebMvcConfigurer cloudWebMvcConfigurer(LoginContextInterceptor loginContextInterceptor){
        return new CloudWebMvcConfigurer(loginContextInterceptor);
    }
    @Bean
    @ConditionalOnMissingBean
    public ThreadLocalExecutor createCloudThreadLocalExecutor(ThreadPoolTaskExecutor executor) {
        return new CloudThreadLocalExecutor(executor);
    }
}
