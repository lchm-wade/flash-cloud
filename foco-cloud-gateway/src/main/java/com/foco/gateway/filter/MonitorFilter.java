package com.foco.gateway.filter;

import com.foco.context.annotation.GatewayTrace;
import com.foco.context.core.FocoContextManager;
import com.foco.context.core.IContext;
import com.foco.model.constant.GatewayOrdersConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 程昭斌
 * @version 1.0
 * @description: 接口监控
 * @date 2019/8/27 14:20
 */
@Slf4j
@GatewayTrace
public class MonitorFilter implements GlobalFilter, Ordered {
    private static final String API_START_TIME = "API-START-TIME";

    @PostConstruct
    public void init() {
        log.info("MonitorFilter is ready to inject, order: {}", getOrder());
    }

    @Override
    public int getOrder() {
        return GatewayOrdersConstants.MONITOR_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        log.info("send {} request to {}", serverHttpRequest.getMethod(), serverHttpRequest.getURI());
        //接口请求开始时间
        exchange.getAttributes().put(API_START_TIME, System.currentTimeMillis());

        return chain.filter(exchange).doFinally((e) -> {
                    Mono.fromRunnable(() -> {
                        Long startTime = exchange.getAttribute(API_START_TIME);
                        Long executeTime = null;
                        if (startTime != null) {
                            executeTime = (System.currentTimeMillis() - startTime);
                        }
                        String msg = String.format("end %s request  %s  http status:%s  接口耗时:%s ms.",
                                serverHttpRequest.getMethod(),
                                serverHttpRequest.getURI(),
                                serverHttpResponse.getStatusCode(),
                                executeTime);
                        log.info(msg);
                    });
                }
        );
    }
}
