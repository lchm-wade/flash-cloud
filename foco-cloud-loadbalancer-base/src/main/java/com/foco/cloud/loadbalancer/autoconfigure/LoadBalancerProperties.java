package com.foco.cloud.loadbalancer.autoconfigure;

import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;
import com.foco.cloud.loadbalancer.ribbon.rule.Rule;
import com.foco.model.constant.FocoConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ChenMing
 * @date 2021/9/16
 */
@ConfigurationProperties(LoadBalancerConstant.LOADBALANCER_PREFIX)
@Setter
@Getter
public class LoadBalancerProperties {

    /**
     * route服务不存在时是否降级访问其他服务（注：相同group、serviceId下的其他服务）
     */
    private boolean demotion = true;

    /**
     * 默认访问route
     */
    private String defaultAccessRoute = FocoConstants.DEFAULT_ROUTE;

    /**
     * 规则
     */
    private Rule rule;
}
