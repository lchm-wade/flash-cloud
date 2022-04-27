package com.foco.cloud.loadbalancer.autoconfigure;

import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenMing
 * @date 2021/9/16
 */
@Configuration
@EnableConfigurationProperties(LoadBalancerProperties.class)
@ConditionalOnProperty(prefix = DiscoveryConstant.DISCOVERY_PREFIX, value = DiscoveryConstant.ENABLED, matchIfMissing = true)
public class LoadBalancerConfiguration {

    @Bean
    @ConditionalOnMissingBean(HeaderThreadLocal.class)
    public HeaderThreadLocal headerThreadLocal() {
        return new HeaderThreadLocal();
    }
}
