package com.foco.cloud.discovery.schedule;

import com.foco.cloud.discovery.factorybean.ServerListenerFactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * @author ChenMing
 * @date 2021/9/1
 */
public class FocoSecondScheduleRegister implements ExtendSecondScheduleRegister {

    private final ConfigurableListableBeanFactory beanFactory;

    public FocoSecondScheduleRegister(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public AbstractBeanDefinition getHolder(String registerId, String group, String serviceId) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(ServerListenerFactoryBean.class);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, registerId);
        argumentValues.addIndexedArgumentValue(1, group);
        argumentValues.addIndexedArgumentValue(2, serviceId);
        argumentValues.addIndexedArgumentValue(3, beanFactory);
        beanDefinition.setConstructorArgumentValues(argumentValues);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        return beanDefinition;
    }

}
