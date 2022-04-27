package com.foco.web.gray.mq;

import com.foco.cloud.discovery.InsTemplate;
import com.foco.mq.constant.MsgPropertyConstant;
import com.foco.mq.extend.impl.RouteBeforeProcessor;
import com.foco.mq.model.Msg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ChenMing
 * @date 2021/11/5
 */
public class GrayRouteBeforeProcessor extends RouteBeforeProcessor {

    @Resource
    protected InsTemplate insTemplate;

    @Value("${spring.application.name:}")
    protected String serviceId;

    @Override
    public boolean postProcessBeforeConsumeSkip(Msg msg) {
        String route = msg.getProperties().get(MsgPropertyConstant.ROUTE);
        //标签逻辑增强，如果说查不到目标route的机器，那么判断该机器是否是default，是则消费
        if (!StringUtils.isEmpty(route) && !getLocalRoute().equalsIgnoreCase(route)) {
            List instances = insTemplate.getInstancesByRoute(serviceId, route);
            if (CollectionUtils.isEmpty(instances) && getDefaultRoute().equals(getLocalRoute())) {
                return false;
            }
        }
        return super.postProcessBeforeConsumeSkip(msg);
    }
}
