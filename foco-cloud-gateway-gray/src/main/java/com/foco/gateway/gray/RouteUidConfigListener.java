package com.foco.gateway.gray;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.schedule.NacosDiscoveryScheduleManager;
import com.foco.cloud.loadbalancer.autoconfigure.LoadBalancerProperties;
import com.foco.gateway.gray.config.GatewayGrayProperties;
import com.foco.gateway.gray.constant.GatewayGrayConstant;
import com.foco.model.constant.FocoConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author ChenMing
 * @date 2021/9/15
 */
@Slf4j
public class RouteUidConfigListener implements InitializingBean, Listener, HeaderRouteChooser {

    private RouteUid routeUid = new RouteUid(FocoConstants.DEFAULT_ROUTE, new HashSet<>());

    public RouteUidConfigListener(Executor executor, NacosDiscoveryScheduleManager manager, GatewayGrayProperties gatewayGrayProperties
            , DiscoveryProperties discoveryProperties, LoadBalancerProperties loadBalancerProperties) {
        this.executor = executor;
        this.manager = manager;
        this.gatewayGrayProperties = gatewayGrayProperties;
        this.discoveryProperties = discoveryProperties;
        this.loadBalancerProperties = loadBalancerProperties;
    }

    protected final Executor executor;

    protected final NacosDiscoveryScheduleManager manager;

    protected final GatewayGrayProperties gatewayGrayProperties;

    protected final DiscoveryProperties discoveryProperties;

    protected final LoadBalancerProperties loadBalancerProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigService config = manager.getConfigService();
        String dataId = gatewayGrayProperties.getRouteDataId();
        String group = discoveryProperties.getGroup();
        config.addListener(dataId, group, this);
        receiveConfigInfo(config.getConfig(dataId, group, 5000));
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        if (StringUtils.isEmpty(configInfo)) {
            return;
        }
        setRouteUid(process(configInfo));
        RouteUid routeUid = process(configInfo);
        if (routeUid.getUid() == null) {
            routeUid.setUid(new HashSet<>());
        }
        //与nacos打印意义不一致，获得处理后的数据结构（可能丢失部分数据）
        log.info("config info processing succeeded by RouteUidConfigListener , config info：{}", JSONObject.toJSONString(routeUid));
        setRouteUid(routeUid);
    }

    public RouteUid process(String configInfo) {
        return JSONObject.parseObject(configInfo, RouteUid.class);
    }

    public void setRouteUid(RouteUid routeUid) {
        this.routeUid = routeUid;
    }

    public RouteUid getRouteUid() {
        return routeUid;
    }


    @Override
    public String filter(Map<String, Collection<String>> headers) {
        Collection<String> uids = headers.get(GatewayGrayConstant.USER_ID);
        String uid = CollectionUtils.isEmpty(uids) ? null : (String) uids.toArray()[0];
        if (StringUtils.isEmpty(routeUid.getStrategy())) {
            return routeUid.getUid().contains(uid) ? routeUid.getRoute() : loadBalancerProperties.getDefaultAccessRoute();
        }
        String route;
        switch (routeUid.getStrategy()) {
            case GatewayGrayConstant.STRATEGY_ALL:
                route = routeUid.getRoute();
                break;
            case GatewayGrayConstant.STRATEGY_NONE:
                route = loadBalancerProperties.getDefaultAccessRoute();
                break;
            default:
                route = loadBalancerProperties.getDefaultAccessRoute();
        }
        return route;
    }
}
