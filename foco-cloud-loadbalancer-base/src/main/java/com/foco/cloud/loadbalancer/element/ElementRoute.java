package com.foco.cloud.loadbalancer.element;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
@Setter
@Getter
public class ElementRoute implements Element {

    @Resource
    private DiscoveryProperties discoveryProperties;

    @Override
    public String get(Map<String, Object> map) {
        return gain(map, LoadBalancerConstant.ROUTE_KEY, discoveryProperties.getRoute());
    }
}
