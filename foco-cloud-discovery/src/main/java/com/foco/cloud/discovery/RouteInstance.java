package com.foco.cloud.discovery;

import lombok.Data;

/**
 * @author ChenMing
 * @date 2021/9/6
 */
@Data
public class RouteInstance<T> {

    private T instance;

    /**
     * 标签Key(标识实例群组，如果都一样给固定字符串即可)
     */
    private String routeKey;
}
