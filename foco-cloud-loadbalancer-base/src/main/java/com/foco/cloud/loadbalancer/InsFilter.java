package com.foco.cloud.loadbalancer;

import com.netflix.loadbalancer.Server;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ChenMing
 * @date 2021/10/22
 */
public interface InsFilter {

    /**
     * 过滤实例(3.x删除并修改)
     * 请使用{@link InsFilter#filter(Map, List)}
     *
     * @param headers 请求头
     * @param ins     传递的实例
     * @return 过滤后你所需要的实例
     */
    @Deprecated
    List<Server> process(Map<String, Collection<String>> headers, List<Server> ins);


    /**
     * 过滤实例
     * TODO 目前两个Api都会被调用
     *
     * @param map 数据
     * @param ins 传递的实例
     * @return 过滤后你所需要的实例
     */
    default List<Server> filter(Map<String, Object> map, List<Server> ins) {
        //default
        return ins;
    }

    /**
     * 顺序(越小优先执行)
     *
     * @return int
     */
    int order();
}
