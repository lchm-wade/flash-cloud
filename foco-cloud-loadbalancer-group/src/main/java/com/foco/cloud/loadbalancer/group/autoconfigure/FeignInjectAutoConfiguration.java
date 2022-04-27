package com.foco.cloud.loadbalancer.group.autoconfigure;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.loadbalancer.autoconfigure.RibbonFocoAutoConfiguration;
import com.foco.cloud.loadbalancer.group.DefaultFeignCrossGroupTransmit;
import com.foco.cloud.loadbalancer.group.InterceptorHelper;
import com.foco.context.util.BootStrapPrinter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.DiscoveryInjectAnnotationPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/10/19 14:31
 */
@AutoConfigureBefore(RibbonFocoAutoConfiguration.class)
public class FeignInjectAutoConfiguration {
    @PostConstruct
    public void init() {
        BootStrapPrinter.log("foco-cloud-loadbalancer-group", this.getClass());
    }

    @Bean
    DiscoveryInjectAnnotationPostProcessor feignInjectAnnotationPostProcessor() {
        return new DiscoveryInjectAnnotationPostProcessor();
    }

    @Bean
    DefaultFeignCrossGroupTransmit defaultFeignCrossGroupTransmit(Environment environment, DiscoveryProperties discoveryProperties) {
        return new DefaultFeignCrossGroupTransmit(environment, discoveryProperties);
    }

    @Bean
    @ConditionalOnMissingBean(InterceptorHelper.class)
    private InterceptorHelper focoInterceptorHelper() {
        return new InterceptorHelper();
    }
}
