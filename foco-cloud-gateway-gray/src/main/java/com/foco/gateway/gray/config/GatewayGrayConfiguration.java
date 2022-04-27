package com.foco.gateway.gray.config;

import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.discovery.schedule.NacosDiscoveryScheduleManager;
import com.foco.cloud.loadbalancer.autoconfigure.LoadBalancerProperties;
import com.foco.cloud.loadbalancer.autoconfigure.RibbonFocoAutoConfiguration;
import com.foco.cloud.loadbalancer.element.ElementRoute;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.context.util.BootStrapPrinter;
import com.foco.gateway.gray.HeaderRouteChooser;
import com.foco.gateway.gray.VersionRouteConfigListener;
import com.foco.gateway.gray.filter.FocoHeaderTransmitFilter;
import com.foco.gateway.gray.filter.GatewayInsGrayFilter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenMing
 * @date 2021/9/13
 */
@Configuration
@EnableConfigurationProperties(GatewayGrayProperties.class)
@ConditionalOnProperty(prefix = DiscoveryConstant.DISCOVERY_PREFIX, value = DiscoveryConstant.ENABLED, matchIfMissing = true)
@ConditionalOnClass(GlobalFilter.class)
@AutoConfigureBefore(value = RibbonFocoAutoConfiguration.class)
public class GatewayGrayConfiguration {
    @PostConstruct
    public void init() {
        BootStrapPrinter.log("foco-cloud-gateway-gray",this.getClass());
    }

    /**
     * 监听线程：事件触发不频繁，只需要一条即可
     */
    @Bean
    public Executor executorByFoco() {
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new NameThreadFactory("nacos-listener"));
    }

    @Bean
    @ConditionalOnMissingBean(ElementRoute.class)
    public GatewayInsGrayFilter gatewayInsGrayFilter(HeaderRouteChooser chooser
            , DiscoveryProperties discoveryProperties
            , HeaderThreadLocal headerThreadLocal
            , LoadBalancerProperties loadBalancerProperties
            , GatewayGrayProperties gatewayGrayProperties) {
        return new GatewayInsGrayFilter(chooser, discoveryProperties, gatewayGrayProperties, loadBalancerProperties, headerThreadLocal);
    }

    @Bean
    @ConditionalOnBean({GatewayInsGrayFilter.class})
    public FocoHeaderTransmitFilter focoHeaderTransmitFilter(DiscoveryProperties discoveryProperties) {
        return new FocoHeaderTransmitFilter(discoveryProperties);
    }

    @Bean
    @ConditionalOnMissingBean(HeaderRouteChooser.class)
    public VersionRouteConfigListener versionRouteConfigListener(Executor executorByFoco,
                                                                 NacosDiscoveryScheduleManager manager,
                                                                 GatewayGrayProperties properties,
                                                                 DiscoveryProperties discoveryProperties,
                                                                 LoadBalancerProperties loadBalancerProperties) {
        return new VersionRouteConfigListener(executorByFoco, manager, properties, discoveryProperties, loadBalancerProperties);
    }

}
