package com.foco.cloud.discovery.schedule;

import com.foco.cloud.discovery.AbstractDiscoveryOwner;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ChenMing
 * @date 2021/6/11
 */
public class FocoServerSchedule extends AbstractSecondSchedule {

    private AbstractDiscoveryOwner instanceOwner;

    private DiscoveryProperties discoveryProperties;

    private AtomicInteger atomic = new AtomicInteger(0);

    private final NacosDiscoveryScheduleManager manager;

    @Override
    public void schedule() {
        boolean hasIns = instanceOwner.refreshServiceInstances(getRegisterId(), getGroup(), getServiceId());
        if (hasIns) {
            atomic.set(0);
        } else {
            if (atomic.incrementAndGet() >= discoveryProperties.getPullEmptyCount()) {
                manager.delSecondSchedules(this);
            }
        }
    }

    public FocoServerSchedule(String registerId, String groupName, String serviceId, ConfigurableListableBeanFactory beanFactory) {
        super(registerId, groupName, serviceId);
        this.discoveryProperties = beanFactory.getBean(DiscoveryProperties.class);
        this.instanceOwner = beanFactory.getBean(AbstractDiscoveryOwner.class);
        this.manager = beanFactory.getBean(NacosDiscoveryScheduleManager.class);
    }

    @Override
    protected Integer getPullInterval() {
        return discoveryProperties.getPullInterval();
    }
}
