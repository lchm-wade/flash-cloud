package com.foco.cloud.loadbalancer.feign;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.context.executor.ThreadLocalTransmit;
import com.foco.context.util.CollectionUtils;
import com.foco.context.util.HttpContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Collection;
import java.util.Map;

/**
 * @author ChenMing
 * @date 2021/9/9
 */
public class FocoFeignInterceptor implements RequestInterceptor, ThreadLocalTransmit<Map<String, Object>> {

    private final DiscoveryProperties grayProperties;

    private final HeaderThreadLocal local;

    public FocoFeignInterceptor(HeaderThreadLocal local
            , DiscoveryProperties grayProperties) {
        this.local = local;
        this.grayProperties = grayProperties;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        local.getLocal().clear();
        requestTemplate.header(grayProperties.getRouteFieldName(), HttpContext.getHeader(grayProperties.getRouteFieldName()));
        Map<String, Collection<String>> headers = requestTemplate.headers();
        headers.keySet().forEach(key -> {
            Collection<String> collection = headers.get(key);
            if (!CollectionUtils.isEmpty(collection)) {
                local.putLocal(key, collection.toArray()[0]);
            }
        });
    }

    @Override
    public void set(Map<String, Object> stringObjectMap) {
        local.setLocal(stringObjectMap);
    }

    @Override
    public Map<String, Object> get() {
        return local.getLocal();
    }

    @Override
    public void remove() {
        local.clear();
    }
}
