package com.foco.gateway.gray.filter;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.loadbalancer.autoconfigure.LoadBalancerProperties;
import com.foco.cloud.loadbalancer.constant.GatewayConstant;
import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;
import com.foco.cloud.loadbalancer.element.ElementRoute;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.gateway.gray.HeaderRouteChooser;
import com.foco.gateway.gray.config.GatewayGrayProperties;
import lombok.Getter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collection;
import java.util.Map;

/**
 * @author ChenMing
 * @date 2021/9/14
 */
@Getter
public class GatewayInsGrayFilter extends ElementRoute {

    private final HeaderRouteChooser chooser;

    private final DiscoveryProperties discoveryProperties;

    private final GatewayGrayProperties gatewayGrayProperties;

    private final HeaderThreadLocal local;

    private final LoadBalancerProperties loadBalancerProperties;

    public GatewayInsGrayFilter(HeaderRouteChooser chooser
            , DiscoveryProperties discoveryProperties
            , GatewayGrayProperties gatewayGrayProperties
            , LoadBalancerProperties loadBalancerProperties
            , HeaderThreadLocal headerThreadLocal) {
        this.chooser = chooser;
        this.discoveryProperties = discoveryProperties;
        this.gatewayGrayProperties = gatewayGrayProperties;
        this.local = headerThreadLocal;
        this.loadBalancerProperties = loadBalancerProperties;
    }

    @Override
    public String get(Map<String, Object> map) {
        String route;
        String routeName = discoveryProperties.getRouteFieldName();
        if (gatewayGrayProperties.isUseInputRoute() && map.get(routeName) instanceof String) {
            route = (String) map.get(routeName);
        } else {
            //暂时兼容旧API，3.x改造
            Object o = map.get(LoadBalancerConstant.OLD_DATA);
            route = this.chooser.filter((Map<String, Collection<String>>) o);
        }
        Map<String, Object> local = this.local.getLocal();
        if (StringUtils.isEmpty(route)) {
            route = loadBalancerProperties.getDefaultAccessRoute();
        }
        Object o = local.get(GatewayConstant.EXCHANGE);
        if (o instanceof ServerWebExchange) {
            ((ServerWebExchange) o).getAttributes().put(discoveryProperties.getRouteFieldName(), route);
        }
        return route;
    }
}
