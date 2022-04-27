package com.foco.cloud.loadbalancer.constant;

import com.foco.model.constant.FocoConstants;

/**
 * @author ChenMing
 * @date 2021/9/16
 */
public interface LoadBalancerConstant {

    String LOADBALANCER_PREFIX = FocoConstants.CONFIG_PREFIX + "loadbalancer";

    String REGISTER_ID_KEY = FocoConstants.CONFIG_PREFIX + "registerId";

    String GROUP_KEY = FocoConstants.CONFIG_PREFIX + "group";

    String ROUTE_KEY = FocoConstants.CONFIG_PREFIX + "route";

    String HINT = "unrealized";

    String OLD_DATA = FocoConstants.CONFIG_PREFIX + "loadbalancer.OldData";
}
