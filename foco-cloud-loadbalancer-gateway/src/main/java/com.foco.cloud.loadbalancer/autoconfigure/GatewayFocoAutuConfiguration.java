package com.foco.cloud.loadbalancer.autoconfigure;

import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.loadbalancer.gateway.FocoLoadBalancerClientFilter;
import com.foco.cloud.loadbalancer.gateway.GatewayTheadLocal;
import com.foco.cloud.loadbalancer.gateway.LoadBalancerSweeperFilter;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenMing
 * @date 2021/10/26
 */
@Configuration
@ConditionalOnProperty(prefix = DiscoveryConstant.DISCOVERY_PREFIX, value = DiscoveryConstant.ENABLED, matchIfMissing = true)
@ConditionalOnClass(GlobalFilter.class)
@AutoConfigureBefore(LoadBalancerConfiguration.class)
public class GatewayFocoAutuConfiguration {

    @Bean
    public FocoLoadBalancerClientFilter focoLoadBalancerClientFilter(GatewayTheadLocal local) {
        return new FocoLoadBalancerClientFilter(local);
    }

    @Bean
    @ConditionalOnMissingBean(HeaderThreadLocal.class)
    public GatewayTheadLocal focoGatewayTheadLocal() {
        return new GatewayTheadLocal();
    }

    @Bean
    @ConditionalOnMissingBean(LoadBalancerSweeperFilter.class)
    public LoadBalancerSweeperFilter focoLoadBalancerSweeperFilter(HeaderThreadLocal local) {
        return new LoadBalancerSweeperFilter(local);
    }
}
