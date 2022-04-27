package com.foco.web.gray;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.loadbalancer.element.ElementRoute;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
public class GrayElementRoute extends ElementRoute {

    private DiscoveryProperties discoveryProperties;

    public GrayElementRoute(DiscoveryProperties discoveryProperties) {
        this.discoveryProperties = discoveryProperties;
    }

    @Override
    public String get(Map<String, Object> map) {
        //web拿gateway处理逻辑后传递的route值
        Object route = map.get(discoveryProperties.getRouteFieldName());
        if (route instanceof String) {
            return (String) route;
        }
        return super.get(map);
    }
}
