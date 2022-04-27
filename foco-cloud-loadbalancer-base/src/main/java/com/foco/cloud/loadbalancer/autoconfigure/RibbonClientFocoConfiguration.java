package com.foco.cloud.loadbalancer.autoconfigure;

import com.foco.cloud.discovery.InsTemplate;
import com.foco.cloud.loadbalancer.InstancePreprocessor;
import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;
import com.foco.cloud.loadbalancer.ribbon.FocoLoadBalancer;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.cloud.loadbalancer.ribbon.rule.FocoRoundRobinRule;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
@Configuration(proxyBeanMethods = false)
public class RibbonClientFocoConfiguration {

    @Resource
    private PropertiesFactory propertiesFactory;

    @RibbonClientName
    private String name = "client";

    @Bean
    @ConditionalOnMissingBean(ILoadBalancer.class)
    public ILoadBalancer focoLoadBalancer(IClientConfig config, IRule rule
            , InsTemplate<Server> template
            , InstancePreprocessor preprocessor
            , HeaderThreadLocal headerThreadLocal) {
        if (this.propertiesFactory.isSet(ILoadBalancer.class, name)) {
            return this.propertiesFactory.get(ILoadBalancer.class, config, name);
        }
        return new FocoLoadBalancer(config, rule, template, preprocessor, headerThreadLocal);
    }

    @Bean
    @ConditionalOnProperty(prefix = LoadBalancerConstant.LOADBALANCER_PREFIX, value = "rule", havingValue = "poll")
    @ConditionalOnMissingBean(IRule.class)
    public FocoRoundRobinRule focoRoundRobinRule() {
        return new FocoRoundRobinRule();
    }
}
