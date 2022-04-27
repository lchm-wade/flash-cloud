package com.foco.gateway.bootstrap;
import com.foco.context.util.BootStrapPrinter;
import com.foco.gateway.autoconfigure.WebCorsAutoConfiguration;
import com.foco.gateway.filter.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import javax.annotation.PostConstruct;

/**
 * description----------
 * @Author lucoo
 * @Date 2021/6/2 14:27
 */
@Slf4j
@Import({WebCorsAutoConfiguration.class})
@Configuration
public class GatewayBootstrapAutoConfiguration {
    @PostConstruct
    public void init() {
        BootStrapPrinter.log("foco-cloud-gateway",this.getClass());
    }
    @Bean
    MonitorFilter monitorFilter(){
        return new MonitorFilter();
    }
    @Bean
    ContextFilter contextFilter(){
        return new ContextFilter();
    }
}
