package com.foco.cloud.loadbalancer.ribbon.rule;

import com.foco.cloud.discovery.utils.DiscoveryUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ChenMing
 * @date 2021/9/17
 */
@Slf4j
public class FocoRoundRobinRule extends AbstractLoadBalancerRule {

    private final AtomicInteger nextServerCyclicCounter = new AtomicInteger(0);

    private ILoadBalancer lb;

    public FocoRoundRobinRule(ILoadBalancer lb) {
        Assert.isTrue(lb != null, "Not found ILoadBalancer by " + FocoRoundRobinRule.class.getName());
        this.lb = lb;
        init();
    }

    public FocoRoundRobinRule() {
        init();
    }

    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        this.lb = lb;
        super.setLoadBalancer(lb);
    }

    private void init() {
        new CounterClearTask(DiscoveryUtils.getScheduledExecutorService(), 1).run();
    }

    private Server choose(ILoadBalancer lb, Object key) {
        List<Server> reachableServers = lb.getReachableServers();
        int upCount = reachableServers.size();
        if (upCount == 0) {
            log.warn("No up servers available from load balancer: " + lb);
            return null;
        }
        int nextServerIndex = incrementAndGetModulo(upCount);
        return reachableServers.get(nextServerIndex);
    }

    /**
     * Inspired by the implementation of {@link AtomicInteger#incrementAndGet()}.
     *
     * @param modulo The modulo to bound the value of the counter.
     * @return The next value.
     */
    private int incrementAndGetModulo(int modulo) {
        for (; ; ) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        return choose(lb, key);
    }

    private class CounterClearTask implements Runnable {

        private final ScheduledExecutorService executor;

        private final int time;

        CounterClearTask(ScheduledExecutorService executor, int time) {
            this.executor = executor;
            this.time = time;
        }

        @Override
        public void run() {
            nextServerCyclicCounter.set(0);
            executor.schedule(this, time, TimeUnit.DAYS);
        }
    }
}
