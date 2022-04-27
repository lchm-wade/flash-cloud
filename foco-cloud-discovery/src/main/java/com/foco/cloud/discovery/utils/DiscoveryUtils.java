package com.foco.cloud.discovery.utils;

import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.foco.cloud.discovery.constants.DiscoveryConstant;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author ChenMing
 * @date 2021/7/17
 */
public final class DiscoveryUtils {

    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new NameThreadFactory("get nacos instance"));

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public static String getBeanName(String registerId, String group, String serviceId) {
        return DiscoveryConstant.DISCOVERY_PREFIX + registerId + DiscoveryConstant.JOINT + group + DiscoveryConstant.JOINT + serviceId + DiscoveryConstant.JOINT;
    }

    public static String getOwnerRouteKey(String registerId, String group, String serviceId, String route) {
        return getOwnerRouteKey(getOwnerKey(registerId, group, serviceId), route);
    }

    public static String getOwnerRouteKey(String ownerKey, String route) {
        return ownerKey + route + DiscoveryConstant.JOINT;
    }

    public static String getOwnerKey(String registerId, String group, String serviceId) {
        return registerId + DiscoveryConstant.JOINT + group + DiscoveryConstant.JOINT + serviceId + DiscoveryConstant.JOINT;
    }
}
