//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.foco.cloud.discovery.config;

import com.foco.cloud.discovery.AbstractDiscoveryOwner;
import com.foco.cloud.discovery.RegistryBeanPostProcessor;
import com.foco.cloud.discovery.ServerDiscoveryOwner;
import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.discovery.schedule.ExtendSecondScheduleRegister;
import com.foco.cloud.discovery.schedule.FocoSecondScheduleRegister;
import com.foco.cloud.discovery.schedule.NacosDiscoveryRefresh;
import com.foco.cloud.discovery.schedule.NacosDiscoveryScheduleManager;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author Xuebin
 */
@Configuration
@EnableConfigurationProperties(DiscoveryProperties.class)
@ConditionalOnProperty(prefix = DiscoveryConstant.DISCOVERY_PREFIX, value = DiscoveryConstant.ENABLED, matchIfMissing = true)
public class DiscoveryConfiguration {

    @Bean
    public RegistryBeanPostProcessor registryBeanPostProcessor() {
        return new RegistryBeanPostProcessor();
    }

    @Bean
    public NacosDiscoveryScheduleManager nacosDiscoveryScheduleManager(DiscoveryProperties grayProperties
            , @Lazy AbstractDiscoveryOwner instanceOwner
            , @Lazy ExtendSecondScheduleRegister register) {
        return new NacosDiscoveryScheduleManager(grayProperties, instanceOwner, register);
    }

    @Bean
    @ConditionalOnMissingBean(ExtendSecondScheduleRegister.class)
    public FocoSecondScheduleRegister defaultSecondScheduleRegister(ConfigurableListableBeanFactory beanFactory) {
        return new FocoSecondScheduleRegister(beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean(AbstractDiscoveryOwner.class)
    public ServerDiscoveryOwner serverGrayInstanceOwner(DiscoveryProperties discoveryProperties,
                                                        NacosDiscoveryScheduleManager manager) {
        return new ServerDiscoveryOwner(discoveryProperties, manager);
    }

    @Bean
    @ConditionalOnMissingBean(NacosDiscoveryRefresh.class)
    public NacosDiscoveryRefresh focoNacosDiscoveryRefresh() {
        return new NacosDiscoveryRefresh();
    }
}
