package com.foco.web.gray.autoconfigure;

import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.context.annotation.ConditionalOnPropertyFoco;
import com.foco.mq.autoconfigure.ExtendBeforeProcessorConfiguration;
import com.foco.mq.extend.impl.RouteBeforeProcessor;
import com.foco.mq.properties.MqProperties;
import com.foco.web.gray.mq.GrayRouteBeforeProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenMing
 * @date 2021/11/23
 */
@Configuration
@ConditionalOnClass({DiscoveryProperties.class, com.foco.mq.autoconfigure.MqConfiguration.class})
@AutoConfigureBefore({ExtendBeforeProcessorConfiguration.class})
public class MqConfiguration {

    @Bean
    @ConditionalOnMissingBean(RouteBeforeProcessor.class)
    @ConditionalOnPropertyFoco(prefix = MqProperties.MQ_PREFIX, name = "labelRoute", matchIfMissing = true)
    public RouteBeforeProcessor webGrayRouteBeforeProcessor() {
        return new GrayRouteBeforeProcessor();
    }
}
