package com.foco.cloud.loadbalancer.ribbon;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.context.common.RequestHeaderTransmit;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author ChenMing
 * @date 2021/11/23
 */
public class RouteRequestHeaderTransmit implements RequestHeaderTransmit {

    @Resource
    private DiscoveryProperties discoveryProperties;

    @Override
    public List<String> transmit() {
        return Collections.singletonList(discoveryProperties.getRouteFieldName());
    }
}
