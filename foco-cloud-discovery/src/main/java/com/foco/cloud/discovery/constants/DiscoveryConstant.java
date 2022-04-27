package com.foco.cloud.discovery.constants;

/**
 * @author ChenMing
 * @date 2021/6/16
 */
public interface DiscoveryConstant {


    /**
     * 拼接符
     */
    String JOINT = "&&";

    /**
     * properties prefix
     */
    String DISCOVERY_PREFIX = "foco.discovery";

    /**
     * 是否生效
     */
    String ENABLED = "enabled";

    /**
     * 默认标签字段命名
     */
    String ROUTE_FIELD_NAME = "route";

    /**
     * 默认实例缓存时间 单位/秒
     */
    int DEFAULT_PULL_INTERVAL = 5;

    /**
     * 默认拉空实例后关闭的判断次数
     */
    int PULL_EMPTY_COUNT = 600;

    /**
     * 默认的registerId
     */
    String DEFAULT_REGISTER_ID = "foco_default";
}
