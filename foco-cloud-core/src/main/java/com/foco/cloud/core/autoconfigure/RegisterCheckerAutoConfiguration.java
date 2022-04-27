package com.foco.cloud.core.autoconfigure;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.foco.cloud.core.registy.InitCheck;
import com.foco.cloud.core.registy.NacosEnvChecker;
import com.foco.cloud.core.registy.RegisterLimitChecker;
import com.foco.cloud.core.registy.RegisterLimitProperties;
import com.foco.properties.DiscoveryPublicProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2022/02/10 14:48
 * @since foco2.3.1
 */
@Configuration
public class RegisterCheckerAutoConfiguration {
    @Bean
    NacosEnvChecker nacosEnvChecker(ObjectProvider<DiscoveryPublicProperties> discoveryPublicProperties, NacosDiscoveryProperties nacosDiscoveryProperties, RegisterLimitProperties registerLimitProperties){
        return new NacosEnvChecker(discoveryPublicProperties.getIfAvailable(),nacosDiscoveryProperties,registerLimitProperties);
    }
    @Bean
    RegisterLimitChecker registerLimitChecker(List<InitCheck> initChecks){
        return new RegisterLimitChecker(initChecks);
    }
}
