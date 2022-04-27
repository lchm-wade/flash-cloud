package com.foco.web.gray.autoconfigure;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.loadbalancer.autoconfigure.RibbonFocoAutoConfiguration;
import com.foco.cloud.loadbalancer.element.ElementRoute;
import com.foco.context.util.BootStrapPrinter;
import com.foco.web.gray.GrayElementRoute;
import com.foco.web.gray.constant.ClassConstant;
import com.netflix.loadbalancer.ILoadBalancer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author ChenMing
 * @date 2021/9/10
 */
@Configuration
@ConditionalOnClass(ILoadBalancer.class)
@ConditionalOnProperty(prefix = DiscoveryConstant.DISCOVERY_PREFIX, value = DiscoveryConstant.ENABLED, matchIfMissing = true)
@ConditionalOnMissingClass(ClassConstant.GATEWAY)
@AutoConfigureBefore({RibbonAutoConfiguration.class, RibbonFocoAutoConfiguration.class})
public class GrayLoadBalancerConfiguration {
    @PostConstruct
    public void init() {
        BootStrapPrinter.log("foco-cloud-web-gray",this.getClass());
    }
    @Bean
    @ConditionalOnMissingBean(ElementRoute.class)
    public ElementRoute elementRoute(DiscoveryProperties discoveryProperties) {
        return new GrayElementRoute(discoveryProperties);
    }
}
