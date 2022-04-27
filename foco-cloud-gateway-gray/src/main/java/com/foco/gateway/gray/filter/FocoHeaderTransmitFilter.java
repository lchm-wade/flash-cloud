package com.foco.gateway.gray.filter;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.model.constant.GatewayOrdersConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ChenMing
 * @date 2021/9/16
 */
public class FocoHeaderTransmitFilter implements GlobalFilter, Ordered {

    private final DiscoveryProperties discoveryProperties;

    public FocoHeaderTransmitFilter(DiscoveryProperties discoveryProperties) {
        this.discoveryProperties = discoveryProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String route = discoveryProperties.getRouteFieldName();
        Object routeValue = exchange.getAttribute(discoveryProperties.getRouteFieldName());
        ServerHttpRequest request = exchange.getRequest().mutate().header(route, (String) routeValue).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return GatewayOrdersConstants.LOAD_HEADER_FILTER_ORDER;
    }
}
