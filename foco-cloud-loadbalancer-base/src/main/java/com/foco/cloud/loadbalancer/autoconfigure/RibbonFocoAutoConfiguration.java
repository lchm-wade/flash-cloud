package com.foco.cloud.loadbalancer.autoconfigure;

import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.loadbalancer.InsFilter;
import com.foco.cloud.loadbalancer.InstancePreprocessor;
import com.foco.cloud.loadbalancer.element.ElementGroup;
import com.foco.cloud.loadbalancer.element.ElementRegisterId;
import com.foco.cloud.loadbalancer.element.ElementRoute;
import com.foco.cloud.loadbalancer.ribbon.FocoInsPreprocessor;
import com.foco.cloud.loadbalancer.ribbon.RouteRequestHeaderTransmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
@Configuration
@ConditionalOnBean(SpringClientFactory.class)
@ConditionalOnClass(RibbonClient.class)
@RibbonClients(defaultConfiguration = RibbonClientFocoConfiguration.class)
@ConditionalOnProperty(prefix = DiscoveryConstant.DISCOVERY_PREFIX, value = DiscoveryConstant.ENABLED, matchIfMissing = true)
public class RibbonFocoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ElementGroup.class)
    public ElementGroup elementGroup() {
        return new ElementGroup();
    }

    @Bean
    @ConditionalOnMissingBean(ElementRegisterId.class)
    public ElementRegisterId elementRegisterId() {
        return new ElementRegisterId();
    }

    @Bean
    @ConditionalOnMissingBean(ElementRoute.class)
    public ElementRoute elementRoute() {
        return new ElementRoute();
    }

    @Bean
    @ConditionalOnMissingBean(RouteRequestHeaderTransmit.class)
    public RouteRequestHeaderTransmit routeRequestHeaderTransmit() {
        return new RouteRequestHeaderTransmit();
    }

    @Bean
    @ConditionalOnMissingBean(InstancePreprocessor.class)
    public FocoInsPreprocessor focoInsFilter(ElementRegisterId registerId,
                                             ElementGroup group,
                                             ElementRoute route,
                                             LoadBalancerProperties loadBalancerProperties,
                                             @Autowired(required = false) List<InsFilter> insFilters) {
        return new FocoInsPreprocessor(registerId, group, route, insFilters, loadBalancerProperties);
    }
}
