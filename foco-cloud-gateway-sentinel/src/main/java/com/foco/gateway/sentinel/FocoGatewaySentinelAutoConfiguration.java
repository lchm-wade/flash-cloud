package com.foco.gateway.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.foco.context.util.BootStrapPrinter;

import javax.annotation.PostConstruct;

/**
 * @author lucoo
 * @version 1.0.0
 * @Description TODO
 * @date 2021-06-24 09:54
 */
public class FocoGatewaySentinelAutoConfiguration {
    @PostConstruct
    public void init(){
        BootStrapPrinter.log("foco-cloud-gateway-sentinel",this.getClass());
        GatewayCallbackManager.setBlockHandler(new FocoGatewaySentinelExceptionHandler());
    }
}
