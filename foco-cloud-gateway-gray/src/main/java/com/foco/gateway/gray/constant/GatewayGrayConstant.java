package com.foco.gateway.gray.constant;

import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;

/**
 * @author ChenMing
 * @date 2021/9/15
 */
public interface GatewayGrayConstant {
    /**
     * 监听dataId
     *
     * @see com.foco.gateway.gray.RouteUidConfigListener
     */
    String ROUTE_DATA_ID = "gray-rout-uid.json";

    String USER_ID = "userId";

    String VERSION = "version";

    String GATEWAY_GRAY_PREFIX = LoadBalancerConstant.LOADBALANCER_PREFIX + ".gateway";

    /**
     * 所有流量
     */
    String STRATEGY_ALL = "ALL";

    /**
     * 不放
     */
    String STRATEGY_NONE = "NONE";

}
