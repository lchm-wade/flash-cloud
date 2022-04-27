package com.foco.cloud.loadbalancer.constant;

import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;

/**
 * @author ChenMing
 * @date 2022/02/23 10:58
 */
public interface GatewayConstant {

    /**
     * {@link HeaderThreadLocal#getLocal()} 的一个key
     */
    String EXCHANGE = "foco.gateway.exchange";
}
