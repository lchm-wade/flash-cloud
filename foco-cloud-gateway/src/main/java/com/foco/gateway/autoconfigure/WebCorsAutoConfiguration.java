package com.foco.gateway.autoconfigure;

import cn.hutool.core.collection.CollectionUtil;
import com.foco.properties.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * description: 跨域配置
 *
 * @Author lucoo
 * @Date 2021/6/2 14:27
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = CorsProperties.PREFIX,name = "enabled",matchIfMissing = true)
public class WebCorsAutoConfiguration {
    @Bean
    public CorsWebFilter corsFilter(CorsProperties corsProperties){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        buildCorsConfiguration(corsProperties,corsConfiguration);
        corsConfiguration.setAllowCredentials(corsProperties.getAllowCredentials());
        source.registerCorsConfiguration(corsProperties.getPath(), corsConfiguration);
        return new CorsWebFilter(source);
    }
    private void buildCorsConfiguration(CorsProperties corsProperties,CorsConfiguration corsConfiguration){
        if(CollectionUtil.isEmpty(corsProperties.getAllowedHeader())){
            corsConfiguration.addAllowedHeader("*");
        }else {
            corsConfiguration.setAllowedHeaders(corsProperties.getAllowedHeader());
        }
        if(CollectionUtil.isEmpty(corsProperties.getAllowedMethod())){
            corsConfiguration.addAllowedMethod("*");
        }else {
            corsConfiguration.setAllowedMethods(corsProperties.getAllowedMethod());
        }
        if(CollectionUtil.isEmpty(corsProperties.getAllowedOrigin())){
            corsConfiguration.addAllowedOrigin("*");
        }else {
            corsConfiguration.setAllowedOrigins(corsProperties.getAllowedOrigin());
        }
        if(CollectionUtil.isNotEmpty(corsProperties.getExposedHeaders())){
            corsConfiguration.setExposedHeaders(corsProperties.getExposedHeaders());
        }
        corsConfiguration.setMaxAge(corsProperties.getMaxAge());
    }
}
