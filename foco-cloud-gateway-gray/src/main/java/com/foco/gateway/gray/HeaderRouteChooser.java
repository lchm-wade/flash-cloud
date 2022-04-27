package com.foco.gateway.gray;

import java.util.Collection;
import java.util.Map;

/**
 * 根据请求来路由route
 *
 * @author ChenMing
 * @date 2021/9/6
 */
public interface HeaderRouteChooser {

    /**
     * 路由到哪个route（3.x改造）
     *
     * @param headers 请求头
     * @return route
     */
    @Deprecated
    String filter(Map<String, Collection<String>> headers);

    /**
     * 路由到哪个route（3.x改造）
     *
     * @param map 所设参数
     * @return route
     */
    default String choose(Map<String, Object> map) {
        //3.x改为此调用
        throw new UnsupportedOperationException("暂未提供服务");
    }
}
