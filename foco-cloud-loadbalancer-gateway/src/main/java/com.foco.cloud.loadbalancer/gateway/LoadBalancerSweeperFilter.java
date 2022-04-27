package com.foco.cloud.loadbalancer.gateway;

import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.model.constant.GatewayOrdersConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ChenMing
 * @date 2022/02/23 16:44
 */
public class LoadBalancerSweeperFilter implements GlobalFilter, Ordered {

    private final HeaderThreadLocal local;

    public LoadBalancerSweeperFilter(HeaderThreadLocal local) {
        this.local = local;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        local.getLocal().clear();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return GatewayOrdersConstants.LOAD_BALANCER_SWEEPER;
    }
}
