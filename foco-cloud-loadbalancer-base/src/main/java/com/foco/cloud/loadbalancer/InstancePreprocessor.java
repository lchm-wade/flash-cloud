package com.foco.cloud.loadbalancer;

import com.foco.cloud.discovery.InsTemplate;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Map;

/**
 * @author ChenMing
 * @date 2021/9/6
 */
public interface InstancePreprocessor {

    /**
     * 固定处理流程
     *
     * @param headers   请求头
     * @param cacheIns  缓存中的所有实例
     * @param serviceId 请求serviceId
     * @return 选定你所需要的实例集合
     */
    List<Server> process(Map<String, Object> headers, InsTemplate<Server> cacheIns, String serviceId);
}
