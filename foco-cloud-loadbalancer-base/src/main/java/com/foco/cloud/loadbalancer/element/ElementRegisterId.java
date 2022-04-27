package com.foco.cloud.loadbalancer.element;

import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;

import java.util.Map;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
public class ElementRegisterId implements Element {

    @Override
    public String get(Map<String, Object> map) {
        return gain(map, LoadBalancerConstant.REGISTER_ID_KEY, DiscoveryConstant.DEFAULT_REGISTER_ID);
    }
}
