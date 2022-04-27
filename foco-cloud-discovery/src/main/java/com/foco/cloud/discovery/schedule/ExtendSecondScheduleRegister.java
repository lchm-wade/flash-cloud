package com.foco.cloud.discovery.schedule;

import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * 当你想要重写Schedule逻辑时，请实现当前接口
 * 可以参考去实现它
 *
 * @author ChenMing
 * @date 2021/9/1
 * @see FocoSecondScheduleRegister
 */
public interface ExtendSecondScheduleRegister {

    /**
     * 获取BeanDefinition
     *
     * @param registerId 注册中心id
     * @param group      实例分组
     * @param serviceId  服务id
     * @return
     */
    AbstractBeanDefinition getHolder(String registerId, String group, String serviceId);
}
