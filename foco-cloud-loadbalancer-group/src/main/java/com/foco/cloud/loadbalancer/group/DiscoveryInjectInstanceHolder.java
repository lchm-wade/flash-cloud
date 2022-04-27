package com.foco.cloud.loadbalancer.group;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucoo
 * @version 1.0.0
 * @description 跨组实例缓存
 * @date 2021/10/28 09:53
 */
public class DiscoveryInjectInstanceHolder {
    private static Map<String,Object> instances=new HashMap<>();
    public static Object getInstance(String registerId,String groupId,String classType){
        return instances.get(registerId+":"+groupId+":"+classType);
    }
    public static void putInstance(String registerId,String groupId,String classType,Object obj){
        instances.put(registerId+":"+groupId+":"+classType,obj);
    }
}
