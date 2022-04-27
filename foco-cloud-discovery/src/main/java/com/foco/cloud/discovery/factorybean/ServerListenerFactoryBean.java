package com.foco.cloud.discovery.factorybean;

import com.foco.cloud.discovery.schedule.FocoServerSchedule;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author ChenMing
 * @date 2021/6/11
 */
@Data
public class ServerListenerFactoryBean implements FactoryBean<FocoServerSchedule> {

    private final String registerId;

    private final String groupName;

    private final String serviceId;

    private final ConfigurableListableBeanFactory beanFactory;

    public ServerListenerFactoryBean(String registerId, String groupName, String serviceId
            , ConfigurableListableBeanFactory beanFactory) {
        this.registerId = registerId;
        this.groupName = groupName;
        this.serviceId = serviceId;
        this.beanFactory = beanFactory;
    }

    @Override
    public FocoServerSchedule getObject() {
        return new FocoServerSchedule(registerId, groupName, serviceId, beanFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return FocoServerSchedule.class;
    }
}
