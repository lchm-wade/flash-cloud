package com.foco.cloud.loadbalancer.autoconfigure;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.loadbalancer.feign.FocoFeignInterceptor;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
@Configuration
@ConditionalOnClass({RequestInterceptor.class, RequestTemplate.class})
@AutoConfigureBefore({LoadBalancerConfiguration.class})
@ConditionalOnProperty(prefix = DiscoveryConstant.DISCOVERY_PREFIX, value = DiscoveryConstant.ENABLED, matchIfMissing = true)
public class FeignFocoAutoConfiguration {

    @Bean
    public RequestInterceptor focoFeignInterceptor(HeaderThreadLocal local
            , DiscoveryProperties grayProperties) {
        return new FocoFeignInterceptor(local, grayProperties);
    }
}
