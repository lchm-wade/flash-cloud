package com.foco.gateway.gray;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

/**
 * 监听nacosConfig使用的对象
 *
 * @author ChenMing
 * @date 2021/9/15
 */
@Getter
@Setter
public class RouteUid {

    /**
     * 去往的route
     */
    private String route;

    /**
     * 用户id
     */
    private HashSet<String> uid;

    /**
     * 策略 用此字段可不设置用户id
     *
     * @see com.foco.gateway.gray.constant.GatewayGrayConstant#STRATEGY_ALL 所有
     * @see com.foco.gateway.gray.constant.GatewayGrayConstant#STRATEGY_NONE 无
     */
    private String strategy;

    public RouteUid(String route, HashSet<String> uid) {
        this.route = route;
        this.uid = uid;
    }

    public RouteUid() {
    }
}
