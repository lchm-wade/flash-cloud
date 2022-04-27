package com.foco.cloud.loadbalancer.gateway;

import com.foco.model.constant.GatewayOrdersConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ChenMing
 * @date 2021/9/13
 */
@Slf4j
public class FocoLoadBalancerClientFilter implements GlobalFilter, Ordered {

    protected final GatewayTheadLocal local;

    public FocoLoadBalancerClientFilter(GatewayTheadLocal theadLocal) {
        this.local = theadLocal;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        local.setLocal(exchange);
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return GatewayOrdersConstants.LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }
}
