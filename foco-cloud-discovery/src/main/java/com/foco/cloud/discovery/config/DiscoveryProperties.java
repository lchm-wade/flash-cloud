package com.foco.cloud.discovery.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foco.cloud.discovery.NacosRegister;
import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.properties.DiscoveryPublicProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ChenMing
 * @date 2021/6/16
 */
@ConfigurationProperties(DiscoveryConstant.DISCOVERY_PREFIX)
@Setter
@Getter
@Slf4j
public class DiscoveryProperties extends DiscoveryPublicProperties {

    /**
     * 是否生效
     */
    private Boolean enabled = true;
    /**
     * 客户端在南天门注册的租户code码
     *
     * @since foco2.2.0新增属性
     */
    private String tenantId;

    /**
     * 需要监听的注册中心
     */
    private List<NacosRegister> register = new ArrayList<>();

    /**
     * header中字段名（注：一般用作请求传入需要去往的route）
     *
     * @see DiscoveryConstant#ROUTE_FIELD_NAME 默认为该字段
     */
    private String routeFieldName;

    /**
     * 实例拉取间隔 默认5s
     *
     * @see DiscoveryConstant#DEFAULT_PULL_INTERVAL
     */
    private Integer pullInterval;

    /**
     * 拉取实例空次数，阈值达到后自动关闭定时拉取任务
     *
     * @see DiscoveryConstant#PULL_EMPTY_COUNT 默认值
     */
    private Integer pullEmptyCount;

    public String getRemoteAddress() {
        return nacosDiscoveryProperties.getServerAddr();
    }

    public String getNamespace() {
        return nacosDiscoveryProperties.getNamespace();
    }

    public String getGroup() {
        return nacosDiscoveryProperties.getGroup();
    }

    @Resource
    @JsonIgnore
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @PostConstruct
    public void init() {
        this.overrideFromEnv();
    }

    private void overrideFromEnv() {
        if (StringUtils.isEmpty(getRouteFieldName())) {
            setRouteFieldName(DiscoveryConstant.ROUTE_FIELD_NAME);
        }
        if (StringUtils.isEmpty(getPullInterval())) {
            setPullInterval(DiscoveryConstant.DEFAULT_PULL_INTERVAL);
        }
        if (getPullEmptyCount() == null || getPullEmptyCount() == 0) {
            setPullEmptyCount(DiscoveryConstant.PULL_EMPTY_COUNT);
        }
        String nacosRoute = nacosDiscoveryProperties.getMetadata().get(getRouteFieldName());
        if (!StringUtils.isEmpty(nacosRoute)) {
            setRoute(nacosRoute);
        } else {
            nacosDiscoveryProperties.getMetadata().put(getRouteFieldName(), getRoute());
        }
        initializeRegister();
    }

    private void initializeRegister() {
        //缺少元素赋予默认值
        int i = 0;
        Set<String> regIds = new HashSet<>();
        for (NacosRegister register : getRegister()) {
            if (regIds.contains(register.getRegisterId())) {
                throw new UnsupportedOperationException("存在重复的registerId：" + register.getRegisterId());
            }
            if (StringUtils.isEmpty(register.getNamespace())) {
                register.setNamespace(getNamespace());
            }
            if (StringUtils.isEmpty(register.getRemoteAddress())) {
                register.setRemoteAddress(getRemoteAddress());
            }
            if (StringUtils.isEmpty(register.getRegisterId())) {
                register.setRegisterId(DiscoveryConstant.DEFAULT_REGISTER_ID);
                if (++i > 1) {
                    log.warn("存在多个未赋registerId配置项，将以最新未赋registerId的配置项设为默认，默认赋值后属性：{}", register.toString());
                }
            }
            if (getRemoteAddress().startsWith("http")) {
                if (StringUtils.isEmpty(register.getUsername())) {
                    register.setUsername(nacosDiscoveryProperties.getUsername());
                }
                if (StringUtils.isEmpty(register.getPassword())) {
                    register.setPassword(nacosDiscoveryProperties.getPassword());
                }
            }
            regIds.add(register.getRegisterId());
        }
        if (getRegister().stream().noneMatch(register -> DiscoveryConstant.DEFAULT_REGISTER_ID.equals(register.getRegisterId()))) {
            NacosRegister register = new NacosRegister();
            register.setRegisterId(DiscoveryConstant.DEFAULT_REGISTER_ID);
            register.setNamespace(getNamespace());
            register.setRemoteAddress(getRemoteAddress());
            if (getRemoteAddress().startsWith("http")) {
                register.setUsername(nacosDiscoveryProperties.getUsername());
                register.setPassword(nacosDiscoveryProperties.getPassword());
            }
            this.getRegister().add(register);
        }
    }

}
