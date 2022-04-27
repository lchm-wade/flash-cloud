package com.foco.cloud.core.bootstrap;

import com.foco.cloud.core.autoconfigure.*;
import com.foco.cloud.core.registy.RegisterLimitProperties;
import com.foco.context.util.BootStrapPrinter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * description----------
 *
 * @Author lucoo
 * @Date 2021/6/2 11:57
 */
@Slf4j
@Import({
        FeignAutoConfiguration.class,
        FeignLogConfiguration.class,
        CloudWebAutoConfiguration.class,
        HystrixAutoConfiguration.class,
        RegisterCheckerAutoConfiguration.class
})
@AutoConfigureBefore(RibbonAutoConfiguration.class)
@EnableConfigurationProperties(RegisterLimitProperties.class)
public class CloudBootstrapAutoConfiguration {
    @PostConstruct
    public void init() {
        BootStrapPrinter.log("foco-cloud-core",this.getClass());
    }
}
