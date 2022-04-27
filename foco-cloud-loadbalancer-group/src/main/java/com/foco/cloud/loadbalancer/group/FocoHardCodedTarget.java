package com.foco.cloud.loadbalancer.group;

import feign.Target;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/10/19 14:24
 */
public class FocoHardCodedTarget extends Target.HardCodedTarget {
    private String group;
    private String registerId;
    public FocoHardCodedTarget( String registerId,String group,Class type,String name,String url) {
        super(type, name,url);
        this.group=group;
        this.registerId=registerId;
    }
    public String getGroup() {
        return group;
    }

    public String getRegisterId() {
        return registerId;
    }
}
