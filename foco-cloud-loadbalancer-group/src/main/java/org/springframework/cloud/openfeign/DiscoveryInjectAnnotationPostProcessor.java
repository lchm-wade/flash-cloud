package org.springframework.cloud.openfeign;

import com.foco.cloud.loadbalancer.group.DiscoveryInject;
import com.foco.cloud.loadbalancer.group.DiscoveryInjectInstanceHolder;
import com.foco.cloud.loadbalancer.group.FocoHardCodedTarget;
import com.foco.context.util.AopTargetUtils;
import com.foco.context.util.ClassUtil;
import com.foco.model.constant.FocoErrorCode;
import com.foco.model.exception.SystemException;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/10/19 14:12
 */
@Slf4j
public class DiscoveryInjectAnnotationPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Field[] fields = ClassUtil.getAllFields(targetClass);
        Map<String, FeignClientFactoryBean> map = applicationContext.getBeansOfType(FeignClientFactoryBean.class);
        for (Field field : fields) {
            DiscoveryInject annotation;
            if (field.isAnnotationPresent(DiscoveryInject.class)) {
                annotation = field.getAnnotation(DiscoveryInject.class);
            } else {
                annotation = candidate(field);
            }
            if (annotation != null) {
                Class<?> type = field.getType();
                Environment environment = applicationContext.getEnvironment();
                String registerId = environment.resolvePlaceholders(annotation.registerId());
                String group = environment.resolvePlaceholders(annotation.group());
                Object target = DiscoveryInjectInstanceHolder.getInstance(registerId, group, type.getName());
                if (target == null) {
                    List<FeignClientFactoryBean> beans = map.values().stream().filter((m) -> m.getType().equals(type)).collect(Collectors.toList());
                    if (beans.isEmpty()) {
                        if (annotation.required()) {
                            SystemException.throwException(FocoErrorCode.SYSTEM_ERROR.getCode(), String.format("没有发现【%s】的实现类,请检查是否有配置@EnableFeignClients扫包,并扫到当前接口对应的类路径", type));
                        } else {
                            return bean;
                        }
                    }
                    try {
                        FeignClientFactoryBean feignClientFactoryBean = beans.get(0);
                        Class<?> targetClz = feignClientFactoryBean.getType();
                        FeignClient feignClient = targetClz.getAnnotation(FeignClient.class);
                        FocoFeignClientFactoryBean focoFeignClientFactoryBean = new FocoFeignClientFactoryBean(type,
                                feignClientFactoryBean.getName(),
                                resolveUrl(feignClient.url(), environment),
                                feignClientFactoryBean.getPath(),
                                feignClientFactoryBean.getContextId(),
                                feignClientFactoryBean.isDecode404(),
                                feignClientFactoryBean.getFallback(),
                                feignClientFactoryBean.getFallbackFactory(),
                                feignClientFactoryBean.getApplicationContext());
                        Object obj = focoFeignClientFactoryBean.getTarget();
                        Field h = obj.getClass().getSuperclass().getDeclaredField("h");
                        h.setAccessible(true);

                        Object proxy = h.get(obj);

                        Field dispatchClazz = proxy.getClass().getDeclaredField("dispatch");
                        dispatchClazz.setAccessible(true);
                        LinkedHashMap dispatchObj = (LinkedHashMap) dispatchClazz.get(proxy);

                        Collection values = dispatchObj.values();
                        for (Object methodHandler : values) {
                            Field buildTemplateFromArgs = methodHandler.getClass().getDeclaredField("buildTemplateFromArgs");
                            buildTemplateFromArgs.setAccessible(true);
                            Object o = buildTemplateFromArgs.get(methodHandler);

                            Field target1;
                            try {
                                target1 = o.getClass().getDeclaredField("target");
                            } catch (Exception e) {
                                target1 = o.getClass().getSuperclass().getDeclaredField("target");
                            }
                            target1.setAccessible(true);
                            Target.HardCodedTarget target1Obj = (Target.HardCodedTarget) target1.get(o);
                            //替换FeignInvocationHandler的target
                            FocoHardCodedTarget focoHardCodedTarget = new FocoHardCodedTarget(registerId,
                                    group, type, target1Obj.name(), target1Obj.url());
                            target1.set(o, focoHardCodedTarget);
                        }
                        //设置
                        field.setAccessible(true);
                        field.set(AopTargetUtils.getTarget(bean), obj);
                        DiscoveryInjectInstanceHolder.putInstance(annotation.registerId(), annotation.group(), type.getName(), obj);
                    } catch (Exception exception) {
                        log.error("DiscoveryInject exception", exception);
                        SystemException.throwException(FocoErrorCode.SYSTEM_ERROR, exception);
                    }
                } else {
                    //设置
                    field.setAccessible(true);
                    try {
                        field.set(AopTargetUtils.getTarget(bean), target);
                    } catch (Exception exception) {
                        log.error("DiscoveryInject exception", exception);
                        SystemException.throwException(FocoErrorCode.SYSTEM_ERROR, exception);
                    }
                }
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String resolveUrl(String urlValue, Environment environment) {
        String url = resolve(urlValue, environment);
        return FeignClientsRegistrar.getUrl(url);
    }

    private String resolve(String value, Environment environment) {
        if (StringUtils.hasText(value)) {
            return environment.resolvePlaceholders(value);
        }
        return value;
    }
    private DiscoveryInject candidate(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Autowired
                    || annotation instanceof Qualifier
                    || annotation instanceof Resource) {
                continue;
            }
            Class<? extends Annotation> annotationType = annotation.annotationType();
            return annotationType.getAnnotation(DiscoveryInject.class);
        }
        return null;
    }
}
