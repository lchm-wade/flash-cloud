package com.foco.cloud.loadbalancer.group;

import com.foco.cloud.discovery.constants.DiscoveryConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscoveryInject {
    String group();
    String registerId() default DiscoveryConstant.DEFAULT_REGISTER_ID;
    boolean required() default true;
}
