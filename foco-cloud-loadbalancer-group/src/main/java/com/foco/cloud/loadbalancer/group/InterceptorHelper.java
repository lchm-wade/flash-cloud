package com.foco.cloud.loadbalancer.group;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.loadbalancer.feign.FocoFeignInterceptor;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.context.common.FeignCrossGroupTransmit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ChenMing
 * @date 2022/02/23 14:17
 */
public class InterceptorHelper implements BeanPostProcessor, BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof FocoFeignInterceptor) {
            Map<String, FeignCrossGroupTransmit> transmitMap = beanFactory.getBeansOfType(FeignCrossGroupTransmit.class);
            List<FeignCrossGroupTransmit> transmits = new ArrayList<>(transmitMap.values().size());
            transmits.addAll(transmitMap.values());
            return new FeignInjectInterceptor((FocoFeignInterceptor) bean, transmits,
                    beanFactory.getBean(NacosDiscoveryProperties.class),
                    beanFactory.getBean(HeaderThreadLocal.class),
                    beanFactory.getBean(DiscoveryProperties.class));
        }
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
