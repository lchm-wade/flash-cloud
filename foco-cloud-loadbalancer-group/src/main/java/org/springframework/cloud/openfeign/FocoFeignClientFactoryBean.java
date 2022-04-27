package org.springframework.cloud.openfeign;

import org.springframework.context.ApplicationContext;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/12/06 14:07
 * @since foco2.1.0
 */
public class FocoFeignClientFactoryBean extends FeignClientFactoryBean{
    public FocoFeignClientFactoryBean(Class<?> type, String name, String url, String path, String contextId, boolean decode404
            , Class<?> fallback, Class<?> fallbackFactory, ApplicationContext applicationContext) {
        setType(type);
        setName(name);
        setUrl(url);
        setPath(path);
        setContextId(contextId);
        setDecode404(decode404);
        setFallback(fallback);
        setFallbackFactory(fallbackFactory);
        setApplicationContext(applicationContext);
    }
}
