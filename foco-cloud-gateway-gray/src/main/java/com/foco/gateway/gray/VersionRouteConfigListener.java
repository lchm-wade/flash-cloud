package com.foco.gateway.gray;

import com.alibaba.fastjson.JSONObject;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.schedule.NacosDiscoveryScheduleManager;
import com.foco.cloud.loadbalancer.autoconfigure.LoadBalancerProperties;
import com.foco.gateway.gray.config.GatewayGrayProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author ChenMing
 * @date 2021/9/18
 */
public class VersionRouteConfigListener extends RouteUidConfigListener {

    private String version;

    public VersionRouteConfigListener(Executor executor, NacosDiscoveryScheduleManager manager
            , GatewayGrayProperties gatewayGrayProperties
            , DiscoveryProperties discoveryProperties
            , LoadBalancerProperties loadBalancerProperties) {
        super(executor, manager, gatewayGrayProperties, discoveryProperties, loadBalancerProperties);
    }

    @Override
    public RouteUid process(String configInfo) {
        VersionRouteUid route = JSONObject.parseObject(configInfo, VersionRouteUid.class);
        //为空字符串直接赋null
        setVersion(StringUtils.isEmpty(route.getVersion()) ? null : route.getVersion());
        return route;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String filter(Map<String, Collection<String>> headers) {
        Collection<String> versions = headers.get(gatewayGrayProperties.getVersionField());
        String version;
        if (CollectionUtils.isEmpty(versions)) {
            version = null;
        } else {
            version = (String) versions.toArray()[0];
            version = "".equals(version) ? null : version;
        }
        if (getVersion() != null && !StringUtils.equals(version, getVersion())) {
            return loadBalancerProperties.getDefaultAccessRoute();
        }
        return super.filter(headers);
    }
}
