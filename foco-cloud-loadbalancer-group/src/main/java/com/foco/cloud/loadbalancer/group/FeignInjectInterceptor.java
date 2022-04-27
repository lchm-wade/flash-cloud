package com.foco.cloud.loadbalancer.group;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.foco.cloud.discovery.NacosRegister;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;
import com.foco.cloud.loadbalancer.element.ElementGroup;
import com.foco.cloud.loadbalancer.feign.FocoFeignInterceptor;
import com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal;
import com.foco.context.asserts.Assert;
import com.foco.context.common.FeignCrossGroupTransmit;
import com.foco.model.api.ApiErrorCode;
import com.foco.model.constant.FocoConstants;
import com.foco.model.constant.FocoErrorCode;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * description: 处理请求上下文传递
 *
 * @Author lucoo
 * @Date 2021/6/2 11:48
 */
@Slf4j
public class FeignInjectInterceptor extends FocoFeignInterceptor {

    public FeignInjectInterceptor(FocoFeignInterceptor delegate, List<FeignCrossGroupTransmit> transmits
            , NacosDiscoveryProperties nacosDiscoveryProperties
            , HeaderThreadLocal local
            , DiscoveryProperties discoveryProperties) {
        super(local, discoveryProperties);
        this.delegate = delegate;
        this.transmits = transmits;
        this.discoveryProperties = discoveryProperties;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    private FocoFeignInterceptor delegate;

    private List<FeignCrossGroupTransmit> transmits;

    private NacosDiscoveryProperties nacosDiscoveryProperties;

    private DiscoveryProperties discoveryProperties;

    @Deprecated
    public FeignInjectInterceptor(List<FeignCrossGroupTransmit> transmits) {
        super(null, null);
        this.transmits = transmits;
        throw new UnsupportedOperationException("已废弃");
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Target<?> target = requestTemplate.feignTarget();
        if (target instanceof FocoHardCodedTarget) {
            FocoHardCodedTarget focoHardCodedTarget = (FocoHardCodedTarget) target;
            String registerId = focoHardCodedTarget.getRegisterId();
            String group = focoHardCodedTarget.getGroup();
            requestTemplate.header(FocoConstants.CROSS_REGISTER_ID_CALL, registerId);
            requestTemplate.header(FocoConstants.CROSS_GROUP_CALL, group);
            requestTemplate.header(LoadBalancerConstant.REGISTER_ID_KEY, registerId);
            requestTemplate.header(LoadBalancerConstant.GROUP_KEY, group);
            if (!group.equals(nacosDiscoveryProperties.getGroup())
                    || !sameRegister(registerId)) {
                //非跨组跨注册中心调用
                for (FeignCrossGroupTransmit transmit : transmits) {
                    List<FeignCrossGroupTransmit.KVPair> kvPairs = transmit.get();
                    for (FeignCrossGroupTransmit.KVPair kvPair : kvPairs) {
                        requestTemplate.header(kvPair.getKey(), kvPair.getValue());
                    }
                }
            }
        }
        delegate.apply(requestTemplate);
    }

    private boolean sameRegister(String registerId) {
        if (DiscoveryConstant.DEFAULT_REGISTER_ID.equals(registerId)) {
            return true;
        }
        Optional<NacosRegister> first = discoveryProperties.getRegister().stream().filter(r -> registerId.equals(r.getRegisterId())).findFirst();
        Assert.that(first.isPresent()).isTrue(String.format("registerId:%s is not exit", registerId), FocoErrorCode.PARAMS_VALID);
        return nacosDiscoveryProperties.getServerAddr().equals(first.get().getRemoteAddress());
    }
}
