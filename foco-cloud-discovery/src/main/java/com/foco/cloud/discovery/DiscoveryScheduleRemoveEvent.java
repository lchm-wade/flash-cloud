package com.foco.cloud.discovery;

import com.foco.cloud.discovery.schedule.AbstractSecondSchedule;
import org.springframework.context.ApplicationEvent;

/**
 * @author ChenMing
 * @date 2022/03/30 18:13
 */
public class DiscoveryScheduleRemoveEvent extends ApplicationEvent {

    private final AbstractSecondSchedule schedule;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DiscoveryScheduleRemoveEvent(AbstractSecondSchedule source) {
        super(source);
        this.schedule = source;
    }

    public AbstractSecondSchedule get() {
        return schedule;
    }
}
