package com.foco.cloud.discovery;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.exception.runtime.NacosRuntimeException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.alibaba.nacos.client.naming.remote.NamingClientProxy;
import com.alibaba.nacos.client.naming.remote.NamingClientProxyDelegate;
import com.alibaba.nacos.client.naming.remote.gprc.NamingGrpcClientProxy;
import com.alibaba.nacos.common.remote.client.RpcClient;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.schedule.NacosDiscoveryScheduleManager;
import com.foco.model.constant.FocoConstants;
import com.google.common.collect.Lists;
import com.netflix.loadbalancer.Server;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChenMing
 * @date 2021/9/6
 */
public class ServerDiscoveryOwner extends AbstractDiscoveryOwner<Server> {


    public ServerDiscoveryOwner(DiscoveryProperties discoveryProperties, NacosDiscoveryScheduleManager manager) {
        super(discoveryProperties, manager);
    }

    @Override
    public List<RouteInstance<Server>> getRouteInstances(String registerId, String group, String serviceId) {
        List<Instance> instances = null;
        NamingService naming = getManager().getNaming(registerId);
        try {
            instances = naming.selectInstances(serviceId, group, null, true, false);
        } catch (NacosException e) {
            if (print(naming)) {
                e.printStackTrace();
                throw new NacosRuntimeException(500, "（断点中遇见可忽略，原因为请求超时机制判断）Nacos实例获取异常" + e.getErrMsg());
            }
        }
        if (CollectionUtils.isEmpty(instances)) {
            return Lists.newLinkedList();
        }
        List<RouteInstance<Server>> instanceList = new ArrayList<>(instances.size());
        instances.forEach(instance -> {
            RouteInstance<Server> server = new RouteInstance<>();
            NacosServer nacosServer = new NacosServer(instance);
            nacosServer.setAlive(instance.isHealthy());
            server.setInstance(nacosServer);
            String route = instance.getMetadata().get(getDiscoveryProperties().getRouteFieldName());
            server.setRouteKey(StringUtils.isEmpty(route) ? FocoConstants.DEFAULT_ROUTE : route);
            instanceList.add(server);
        });
        return instanceList;
    }

    /**
     * 是否打印
     *
     * @param namingService naming实现
     * @return true：打印 false：不打印
     */
    private boolean print(NamingService namingService) {
        try {
            if (namingService instanceof NacosNamingService) {
                Field clientProxyField = NacosNamingService.class.getDeclaredField("clientProxy");
                clientProxyField.setAccessible(true);
                Object clientProxy = clientProxyField.get(namingService);
                //目前只应用了GRpc，所以只考虑这个场景
                if (clientProxy instanceof NamingClientProxyDelegate) {
                    Field grpcClientProxyField = NamingClientProxyDelegate.class.getDeclaredField("grpcClientProxy");
                    grpcClientProxyField.setAccessible(true);
                    Object grpcClientProxy = grpcClientProxyField.get(clientProxy);
                    if (grpcClientProxy instanceof NamingGrpcClientProxy) {
                        Field rpcClientField = NamingGrpcClientProxy.class.getDeclaredField("rpcClient");
                        rpcClientField.setAccessible(true);
                        Object rpcClient = rpcClientField.get(grpcClientProxy);
                        if (rpcClient instanceof RpcClient && ((RpcClient) rpcClient).isShutdown()) {
                            return false;
                        }
                    }

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }
}
