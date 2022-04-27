package com.foco.cloud.core.registy;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.foco.model.constant.FocoErrorCode;
import com.foco.model.exception.ApiException;
import com.foco.properties.DiscoveryPublicProperties;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2022/02/10 13:38
 * @since foco2.3.1
 */
public class NacosEnvChecker implements InitCheck{
    private DiscoveryPublicProperties discoveryPublicProperties;
    private RegisterLimitProperties registerLimitProperties;
    String namespace;
    public NacosEnvChecker(DiscoveryPublicProperties discoveryPublicProperties,NacosDiscoveryProperties nacosDiscoveryProperties,RegisterLimitProperties registerLimitProperties) {
        this.discoveryPublicProperties = discoveryPublicProperties;
        this.registerLimitProperties=registerLimitProperties;
        namespace=nacosDiscoveryProperties.getNamespace();
    }

    @Override
    public boolean check() {
        if(!isLocalMachine()){
            return true;
        }
        if(isLabelRoute()){
            return true;
        }
        //找出对应的环境映射配置
        if(registerLimitProperties.getLocal().getNamespace().equals(namespace)){
            return !registerLimitProperties.getLocal().isLimitRegister();
        }
        if(registerLimitProperties.getDev().getNamespace().equals(namespace)){
            return !registerLimitProperties.getDev().isLimitRegister();
        }
        if(registerLimitProperties.getTest().getNamespace().equals(namespace)){
            return !registerLimitProperties.getTest().isLimitRegister();
        }
        if(registerLimitProperties.getFix().getNamespace().equals(namespace)){
            return !registerLimitProperties.getFix().isLimitRegister();
        }
        if(registerLimitProperties.getProd().getNamespace().equals(namespace)){
            return !registerLimitProperties.getProd().isLimitRegister();
        }
        return false;
    }

    @Override
    public ApiException throwException() {
        return new ApiException(FocoErrorCode.CONFIG_VALID.getCode(),String.format("本地环境不允许注册到:%s",namespace));
    }
    private boolean isLabelRoute(){
        if(discoveryPublicProperties!=null
                && !DiscoveryPublicProperties.DEFAULT_ROUTE.equals(discoveryPublicProperties.getRoute())){
            return true;
        }
        return false;
    }
    private boolean isLocalMachine(){
        String os = System.getProperty("os.name").toLowerCase();
        return os.startsWith("windows")||os.startsWith("mac");
    }
}
