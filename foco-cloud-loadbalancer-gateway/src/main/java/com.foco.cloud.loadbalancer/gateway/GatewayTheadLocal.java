package com.foco.cloud.loadbalancer.gateway;

import com.foco.cloud.loadbalancer.constant.GatewayConstant;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.context.util.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * @author ChenMing
 * @date 2022/02/23 11:05
 */
public class GatewayTheadLocal extends HeaderThreadLocal {

    public void setLocal(ServerWebExchange exchange) {
        getLocal().clear();
        HttpHeaders headers = exchange.getRequest().getHeaders();
        for (String key : headers.keySet()) {
            List<String> val = headers.get(key);
            if (!CollectionUtils.isEmpty(val)) {
                putLocal(key, val.get(0));
            }
        }
        putLocal(GatewayConstant.EXCHANGE, exchange);
    }

}
