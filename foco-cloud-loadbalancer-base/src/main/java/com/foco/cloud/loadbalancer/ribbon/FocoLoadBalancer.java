package com.foco.cloud.loadbalancer.ribbon;

import com.foco.cloud.discovery.AbstractDiscoveryOwner;
import com.foco.cloud.discovery.InsTemplate;
import com.foco.cloud.loadbalancer.InstancePreprocessor;
import com.foco.cloud.loadbalancer.ribbon.rule.FocoRoundRobinRule;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * @author ChenMing
 * @date 2021/9/2
 */
public class FocoLoadBalancer implements ILoadBalancer {

    private final IRule rule;

    private final InsTemplate<Server> instanceOwner;

    private final InstancePreprocessor instancePreprocessor;

    private final IClientConfig iClientConfig;

    private final HeaderThreadLocal local;

    public FocoLoadBalancer(IClientConfig iClientConfig, IRule rule
            , InsTemplate<Server> instanceOwner
            , InstancePreprocessor instancePreprocessor
            , HeaderThreadLocal local) {
        this.rule = rule == null ? new FocoRoundRobinRule(this) : rule;
        if (this.rule.getLoadBalancer() == null) {
            this.rule.setLoadBalancer(this);
        }
        this.instanceOwner = instanceOwner;
        this.iClientConfig = iClientConfig;
        this.instancePreprocessor = instancePreprocessor;
        this.local = local;
    }

    /**
     * todo 只关注健康的实例
     */
    @Override
    public List<Server> getAllServers() {
        return getReachableServers();
    }

    @Override
    public List<Server> getReachableServers() {
        return instancePreprocessor.process(local.getLocal(), instanceOwner, iClientConfig.getClientName());
    }

    /**
     * 实例由AbstractGrayInstanceOwner管理
     *
     * @param newServers 更新的实例列表
     * @see AbstractDiscoveryOwner
     */
    @Override
    public void addServers(List<Server> newServers) {
        //不关注
    }

    @Override
    public Server chooseServer(Object key) {
        return rule.choose(key);
    }

    /**
     * 通知注册中心下线
     *
     * @param server 当前实例
     */
    @Override
    public void markServerDown(Server server) {
    }

    /**
     * todo 废除
     *
     * @param availableOnly true：只关注可用的 false：all
     */
    @Override
    public List<Server> getServerList(boolean availableOnly) {
        return availableOnly ? getReachableServers() : getAllServers();
    }

}
