package com.foco.cloud.loadbalancer.ribbon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 由于相关开源框架参数设置并不灵活，故以线程绑定变量的形式进行传参
 * TODO 该命名在后期维护的时候并不贴切，可以在3.x后改名
 *
 * @author ChenMing
 * @date 2021/10/21
 */
public class HeaderThreadLocal {

    private final ThreadLocal<Map<String, Object>> local = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * 子类处理后返回集合(废弃),将不被使用
     *
     * @return 一般是header
     */
    @Deprecated
    public Map<String, Object> getHeaders() {
        throw new UnsupportedOperationException("已废弃");
    }

    public Map<String, Object> getLocal() {
        return local.get();
    }

    public void setLocal(Map<String, Object> obj) {
        this.local.set(obj);
    }

    public void putLocal(String key, Object val) {
        this.local.get().put(key, val);
    }

    public void clear() {
        local.remove();
    }
}
