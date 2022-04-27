package com.foco.gateway.gray.config;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.gateway.gray.constant.GatewayGrayConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author ChenMing
 * @date 2021/9/15
 */
@ConfigurationProperties(GatewayGrayConstant.GATEWAY_GRAY_PREFIX)
@Setter
@Getter
public class GatewayGrayProperties {

    /**
     * 是否使用前端输入的route
     *
     * @see DiscoveryProperties#getRouteFieldName() 字段名
     * 一般来说开启true用作链路优先路由
     */
    private boolean useInputRoute;

    /**
     * 默认的nacos监听dataId
     *
     * @see com.foco.gateway.gray.RouteUidConfigListener
     */
    private String routeDataId;

    /**
     * 版本字段名称
     *
     * @see GatewayGrayConstant#VERSION 默认
     */
    private String versionField;

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(getRouteDataId())) {
            setRouteDataId(GatewayGrayConstant.ROUTE_DATA_ID);
        }
        if(StringUtils.isEmpty(getVersionField())){
            setVersionField(GatewayGrayConstant.VERSION);
        }
    }
}
