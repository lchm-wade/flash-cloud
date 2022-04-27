package com.foco.cloud.loadbalancer.element;

import com.foco.cloud.loadbalancer.constant.LoadBalancerConstant;
import com.foco.context.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
interface Element {
    /**
     * 返回元素内容（已废弃，暂时保持原有特性，3.x废弃）
     *
     * @param headers 请求头
     * @return
     * @see #get(Map)
     */
    @Deprecated
    default String value(Map<String, Collection<String>> headers) {
        throw new UnsupportedOperationException("已废弃");
    }


    /**
     * 从{@param headers}获取值，键为{@param key}（已废弃，暂时保持原有特性，3.x废弃）
     *
     * @param headers 请求头
     * @param key     header的key
     * @return 值
     */
    @Deprecated
    default String get(Map<String, Collection<String>> headers, String key) {
        Collection<String> value = headers.get(key);
        if (!CollectionUtils.isEmpty(value)) {
            return String.valueOf(value.toArray()[0]);
        }
        return null;
    }

    /**
     * 返回元素内容
     * TODO 设为default原因为保证实现该接口的用户不需要改动
     *
     * @param map 数据{@link com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal}存放
     * @return
     */
    default String get(Map<String, Object> map) {
        return LoadBalancerConstant.HINT;
    }

    /**
     * 从{@param headers}获取值，键为{@param key}
     *
     * @param map    包装数据{@link com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal}
     * @param key    key
     * @param defVal {@param map}获取的值为空时返回该值
     * @return 值
     */
    default String gain(Map<String, Object> map, String key, String defVal) {
        return gain(map, key, () -> defVal);
    }

    /**
     * 从{@param headers}获取值，键为{@param key}
     *
     * @param map    包装数据{@link com.foco.cloud.loadbalancer.ribbon.HeaderThreadLocal}
     * @param key    key
     * @param defVal {@param map}获取的值为空时返回该值
     * @return 值
     */
    default String gain(Map<String, Object> map, String key, Supplier<String> defVal) {
        Object value = map.get(key);
        if (value instanceof String) {
            String result = (String) value;
            return result.isEmpty() ? defVal.get() : result;
        }
        return defVal.get();
    }
}
