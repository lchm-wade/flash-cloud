package com.foco.cloud.discovery.schedule;

import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.discovery.utils.DiscoveryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @author ChenMing
 * @date 2021/7/17
 */
@Slf4j
public abstract class AbstractSecondSchedule implements Runnable {

    private boolean stop = false;

    private final String registerId;

    private final String serviceId;

    private final String group;

    @Override
    public void run() {
        if (stop) {
            return;
        }
        try {
            schedule();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("定时拉取实例异常：" + e.getMessage());
        } finally {
            //scheduleAtFixedRate一旦报错就会停止轮询
            DiscoveryUtils.getScheduledExecutorService().schedule(this, getPullInterval(), TimeUnit.SECONDS);
        }
    }

    public void cease() {
        stop = true;
    }

    protected AbstractSecondSchedule(String registerId, String group, String serviceId) {
        Assert.notNull(registerId, "注册中心id不能为空");
        Assert.notNull(serviceId, "服务id不能为空");
        Assert.notNull(group, "分组名不能为空");
        this.registerId = registerId;
        this.serviceId = serviceId;
        this.group = group;
    }

    protected Integer getPullInterval() {
        return DiscoveryConstant.DEFAULT_PULL_INTERVAL;
    }

    /**
     * 调度内容
     */
    protected abstract void schedule();

    /**
     * 服务id
     *
     * @return 服务id
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * 注册中心id
     *
     * @return registerId
     */
    public String getRegisterId() {
        return registerId;
    }

    /**
     * 服务分组
     *
     * @return 服务分组
     */
    public String getGroup() {
        return group;
    }
}
