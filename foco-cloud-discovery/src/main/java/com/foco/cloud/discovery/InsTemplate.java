package com.foco.cloud.discovery;

import java.util.List;
import java.util.Set;

/**
 * @author ChenMing
 * @date 2021/9/29
 */
public interface InsTemplate<T> {

    /**
     * 根据服务id拉取实例
     *
     * @param serviceId 服务id
     * @return 对应实例集合
     */
    List<T> getInstances(String serviceId);

    /**
     * 根据分组+服务名查找服务实例集合（如果你没有设置过route相关元素，那么此api就是你想要的）
     *
     * @param registerId 注册中心id
     * @param serviceId  服务id
     * @param group      分组
     * @return 对应实例集合
     */
    List<T> getInstances(String registerId, String group, String serviceId);

    /**
     * 根据服务名 + route 查找服务实例集合
     *
     * @param serviceId 服务id
     * @param route     路由标签
     * @return 对应实例集合
     */
    List<T> getInstancesByRoute(String serviceId, String route);

    /**
     * 根据分组 + 服务名 + route 查找服务实例集合
     *
     * @param registerId 注册中心id
     * @param serviceId  服务id
     * @param group      分组
     * @param route      路由标签
     * @return 对应实例集合
     */
    List<T> getInstancesByRoute(String registerId, String group, String serviceId, String route);

    /**
     * 已注册定时任务的监听列表
     *
     * @return 监听中的集合  registerId + && + group + && + serviceId
     */
    Set<String> getRegistered();
}
