package com.foco.cloud.discovery.schedule;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author ChenMing
 * @date 2022/03/30 16:41
 */
public class NacosDiscoveryRefresh implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {

    /**
     * 为了满足动态监听配置文件变化
     * <p>spring监听的{@link EnvironmentChangeEvent}无法保证在{@link DiscoveryProperties}
     * 初始化之后执行，故通过后置处理器进行临时缓存
     * <p> 逻辑：后置处理器一旦经过{@link DiscoveryProperties},将放入temp
     * 随后下一个bean进入判断{@code temp}有值后，开始进行初始化
     */
    private DiscoveryProperties temp;

    private ApplicationReadyEvent event;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //此处没有用postProcessAfterInitialization主要原因是因为可能该properties并未走完所有的加持（包括业务侧自身的）
        if (temp != null) {
            //event！=null说明时容器初始化完成了之后的再refresh
            if (event != null) {
                ConfigurableApplicationContext context = event.getApplicationContext();
                context.getBean(NacosDiscoveryScheduleManager.class).refresh();
            }
            temp = null;
        }
        if (bean instanceof DiscoveryProperties && event != null) {
            temp = (DiscoveryProperties) bean;
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.event = event;
    }
}
