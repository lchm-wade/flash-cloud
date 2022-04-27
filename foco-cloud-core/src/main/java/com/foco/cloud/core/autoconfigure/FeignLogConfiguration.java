package com.foco.cloud.core.autoconfigure;

import feign.Logger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 设置feign 日志级别，用于调试
 * @Author lucoo
 * @Date 2021/6/2 10:37
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties("feign.logging")
public class FeignLogConfiguration {

    private Logger.Level level=Logger.Level.NONE;

    @Bean
    Logger.Level feignLoggerLevel(){
        log.info("Feign Log Level is {}",level);
        return level;
    }
}
