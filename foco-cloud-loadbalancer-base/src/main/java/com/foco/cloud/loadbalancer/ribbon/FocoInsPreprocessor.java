package com.foco.cloud.loadbalancer.ribbon;

import com.foco.cloud.discovery.InsTemplate;
import com.foco.cloud.loadbalancer.InsFilter;
import com.foco.cloud.loadbalancer.InstancePreprocessor;
import com.foco.cloud.loadbalancer.autoconfigure.LoadBalancerProperties;
import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;
import com.foco.cloud.loadbalancer.element.ElementGroup;
import com.foco.cloud.loadbalancer.element.ElementRegisterId;
import com.foco.cloud.loadbalancer.element.ElementRoute;
import com.netflix.loadbalancer.Server;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ChenMing
 * @date 2021/9/6
 */
public class FocoInsPreprocessor implements InstancePreprocessor {

    private final ElementRegisterId registerId;

    private final ElementGroup group;

    private final ElementRoute route;

    private final List<InsFilter> insFilters;

    private final LoadBalancerProperties lbProperties;

    public FocoInsPreprocessor(ElementRegisterId registerId, ElementGroup group, ElementRoute route, List<InsFilter> insFilters, LoadBalancerProperties lbProperties) {
        this.registerId = registerId;
        this.group = group;
        this.route = route;
        this.lbProperties = lbProperties;
        if (CollectionUtils.isEmpty(insFilters)) {
            insFilters = new ArrayList<>();
        }
        this.insFilters = insFilters.stream().sorted(Comparator.comparing(InsFilter::order)).collect(Collectors.toList());
    }

    @Override
    public List<Server> process(Map<String, Object> map, InsTemplate<Server> cacheIns, String serviceId) {
        //暂时兼容相关已开放API，后续（3.x）请改成新Api进行调用（放入map中给予下面链路中需要的扩展）
        Map<String, Collection<String>> headers = new HashMap<>(map.size());
        for (String key : map.keySet()) {
            Object o = map.get(key);
            if (o instanceof String) {
                headers.put(key, Collections.singletonList((String) o));
            } else if (o instanceof Collection) {
                Collection val = (Collection) o;
                if (!CollectionUtils.isEmpty(val) && val.toArray()[0].getClass() == String.class) {
                    headers.put(key, val);
                }
            }
        }
        map.put(LoadBalancerConstant.OLD_DATA, headers);
        String routeTag = route.get(map);
        String rId = registerId.get(map);
        String g = group.get(map);
        List<Server> ins = cacheIns.getInstancesByRoute(rId, g, serviceId, routeTag);
        //route降级
        if (CollectionUtils.isEmpty(ins) && lbProperties.isDemotion() && !lbProperties.getDefaultAccessRoute().equals(routeTag)) {
            ins = cacheIns.getInstancesByRoute(rId, g, serviceId, lbProperties.getDefaultAccessRoute());
        }
        if (!CollectionUtils.isEmpty(insFilters)) {
            for (InsFilter insFilter : insFilters) {
                //（3.x）请删除process Api调用，目前两个都被调用，两个都被实现，需实现者保证不会有逻辑干扰
                ins = insFilter.process(headers, ins);
                ins = insFilter.filter(map, ins);
            }
        }
        return ins;
    }
}
