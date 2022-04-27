package com.foco.gateway.filter;

import com.foco.context.core.FocoContextManager;
import com.foco.context.core.LoginContextHolder;
import com.foco.model.constant.GatewayOrdersConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/11/24 10:18
 */
public class ContextFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest newRequest = exchange.getRequest().mutate().headers((headers)->{
            FocoContextManager.setHeader((header, context)->headers.put(header, Arrays.asList(context)));
        }).build();
        FocoContextManager.remove();
        LoginContextHolder.remove();
        //构建新的ServerWebExchange实例
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return GatewayOrdersConstants.CONTEXT_FILTER;
    }
}
