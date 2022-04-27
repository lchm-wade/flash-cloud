package com.foco.cloud.loadbalancer.group;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.context.common.FeignCrossGroupTransmit;
import com.foco.context.core.Env;
import com.foco.model.constant.FocoConstants;
import com.google.common.collect.Lists;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author lucoo
 * @version 1.0.0
 * @description 跨组调用安全管控传递客户端参数
 * @date 2021/12/08 13:38
 * @since foco2.2.0
 */
public class DefaultFeignCrossGroupTransmit implements FeignCrossGroupTransmit {
    String serviceName;
    String tenantId;
    private static final String APPLICATION_NAME="spring.application.name";

    public DefaultFeignCrossGroupTransmit(Environment environment,DiscoveryProperties discoveryProperties) {
        this.serviceName = environment.getProperty(APPLICATION_NAME);
        tenantId=discoveryProperties.getTenantId();
    }

    @Override
    public List<KVPair> get() {
        KVPair serviceNameKv=new KVPair(FocoConstants.CROSS_SERVICE_NAME_CALL,serviceName);
        KVPair tenantKv=new KVPair(FocoConstants.CROSS_TENANT_NAME_CALL,tenantId);
        return Lists.newArrayList(serviceNameKv,tenantKv);
    }
}
