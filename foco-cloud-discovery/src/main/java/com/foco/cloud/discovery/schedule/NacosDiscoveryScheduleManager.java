package com.foco.cloud.discovery.schedule;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingService;
import com.foco.cloud.discovery.AbstractDiscoveryOwner;
import com.foco.cloud.discovery.DiscoveryScheduleRemoveEvent;
import com.foco.cloud.discovery.NacosRegister;
import com.foco.cloud.discovery.RegistryBeanPostProcessor;
import com.foco.cloud.discovery.config.DiscoveryProperties;
import com.foco.cloud.discovery.constants.DiscoveryConstant;
import com.foco.cloud.discovery.utils.DiscoveryUtils;
import com.foco.context.util.BeanCopierEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenMing
 * @date 2021/6/11
 */
@Slf4j
public final class NacosDiscoveryScheduleManager implements InitializingBean, ApplicationListener<ApplicationReadyEvent> {

    private Map<String, NamingService> naming = new ConcurrentHashMap<>();

    private ConfigService configService;

    private Map<String, AbstractSecondSchedule> schedules = new ConcurrentHashMap<>(64);

    public NacosDiscoveryScheduleManager(DiscoveryProperties discoveryProperties, AbstractDiscoveryOwner instanceOwner, ExtendSecondScheduleRegister scheduleRegister) {
        this.discoveryProperties = discoveryProperties;
        this.instanceOwner = instanceOwner;
        this.scheduleRegister = scheduleRegister;
    }

    private final DiscoveryProperties discoveryProperties;

    private final AbstractDiscoveryOwner instanceOwner;

    private final ExtendSecondScheduleRegister scheduleRegister;

    private ConfigurableApplicationContext context;

    /**
     * 之前初始化时的Register，如果为空说明第一次初始化
     */
    private List<NacosRegister> preNacosRegisters;

    /**
     * 之前初始化时的 NacosDiscoveryProperties，如果为空说明第一次初始化
     */
    private NacosDiscoveryProperties preNacosDiscoveryProperties;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(preNacosRegisters)) {
            List<NacosRegister> registers = discoveryProperties.getRegister();
            for (NacosRegister register : registers) {
                Properties properties = getProperties(register);
                naming.put(register.getRegisterId(), NacosFactory.createNamingService(properties));
            }
            preNacosRegisters = new ArrayList<>(registers);
        }
        if (preNacosDiscoveryProperties == null) {
            preNacosDiscoveryProperties = BeanCopierEx.copyProperties(discoveryProperties.getNacosDiscoveryProperties(), NacosDiscoveryProperties.class);
            Properties properties = getProperties(preNacosDiscoveryProperties);
            configService = NacosFactory.createConfigService(properties);
        }
    }

    private Properties getProperties(NacosDiscoveryProperties properties) {
        NacosRegister register = new NacosRegister();
        register.setRemoteAddress(properties.getServerAddr());
        register.setNamespace(properties.getNamespace());
        register.setUsername(properties.getUsername());
        register.setPassword(properties.getPassword());
        return getProperties(register);
    }

    private Properties getProperties(NacosRegister register) {
        Properties properties = new Properties();
        if (register.getRemoteAddress().startsWith("http")) {
            if (!StringUtils.isEmpty(register.getUsername())) {
                properties.put(PropertyKeyConst.USERNAME, register.getUsername());
            }
            if (!StringUtils.isEmpty(register.getPassword())) {
                properties.put(PropertyKeyConst.PASSWORD, register.getPassword());
            }
        }
        properties.put(PropertyKeyConst.SERVER_ADDR, register.getRemoteAddress());
        properties.put(PropertyKeyConst.NAMESPACE, register.getNamespace());
        return properties;
    }

    public NamingService getNaming() {
        return naming.get(DiscoveryConstant.DEFAULT_REGISTER_ID);
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public NamingService getNaming(String registerId) {
        return naming.get(registerId);
    }

    public List<AbstractSecondSchedule> getSecondSchedules() {
        return new ArrayList<>(schedules.values());
    }

    public void putSecondSchedules(List<AbstractSecondSchedule> secondSchedules) {
        secondSchedules.forEach(this::putSecondSchedules);
    }

    public void putSecondSchedules(AbstractSecondSchedule secondSchedule) {
        final String key = DiscoveryUtils.getOwnerKey(secondSchedule.getRegisterId(), secondSchedule.getGroup(), secondSchedule.getServiceId());
        if (schedules.containsKey(key)) {
            delSecondSchedules(secondSchedule);
        }
        DiscoveryUtils.getScheduledExecutorService().schedule(secondSchedule, DiscoveryConstant.DEFAULT_PULL_INTERVAL, TimeUnit.SECONDS);
        schedules.put(key, secondSchedule);
    }

    public void delSecondSchedules(AbstractSecondSchedule secondSchedule) {
        final String key = DiscoveryUtils.getOwnerKey(secondSchedule.getRegisterId(), secondSchedule.getGroup(), secondSchedule.getServiceId());
        instanceOwner.getRegistered().remove(key);
        schedules.get(key).cease();
        String beanName = DiscoveryUtils.getBeanName(secondSchedule.getRegisterId(), secondSchedule.getGroup(), secondSchedule.getServiceId());
        RegistryBeanPostProcessor.getBeanFactory().destroyBean(beanName, secondSchedule);
        RegistryBeanPostProcessor.getRegistry().removeBeanDefinition(beanName);
        schedules.remove(key);
        context.publishEvent(new DiscoveryScheduleRemoveEvent(secondSchedule));
    }

    public synchronized void putSecondSchedulesByGroup(String registerId, String group, String serviceId) {
        BeanDefinitionRegistry registry = RegistryBeanPostProcessor.getRegistry();
        AbstractBeanDefinition beanDefinition = scheduleRegister.getHolder(registerId, group, serviceId);
        String beanName = DiscoveryUtils.getBeanName(registerId, group, serviceId);
        if (!registry.isBeanNameInUse(beanName)) {
            BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName);
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
            AbstractSecondSchedule schedule = (AbstractSecondSchedule) RegistryBeanPostProcessor
                    .getBeanFactory().getBean(beanName);
            putSecondSchedules(schedule);
            log.info("新增监听器，监听器group：{}，serviceId：{}", group, serviceId);
        }
    }


    public synchronized void refresh() {
        try {
            //NacosRegisters重新加载
            refreshNacosRegisters();
            //NacosDiscoveryProperties重新加载
            refreshNacosDiscoveryProperties();
            //owner初始化
            instanceOwner.initializeProperty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshNacosDiscoveryProperties() throws Exception {
        //之前初始化时的 NacosDiscoveryProperties，如果为空说明第一次初始化
        if (preNacosDiscoveryProperties == null) {
            afterPropertiesSet();
        } else {
            NacosDiscoveryProperties nacosDiscoveryProperties = discoveryProperties.getNacosDiscoveryProperties();
            if (!preNacosDiscoveryProperties.equals(nacosDiscoveryProperties)) {
                Properties properties = getProperties(preNacosDiscoveryProperties);
                ConfigService configService = getConfigService();
                this.configService = NacosFactory.createConfigService(properties);
                configService.shutDown();
                preNacosDiscoveryProperties = BeanCopierEx.copyProperties(nacosDiscoveryProperties, NacosDiscoveryProperties.class);
            }
        }
    }

    private void refreshNacosRegisters() throws Exception {
        //之前初始化时的Register，如果为空说明第一次初始化
        if (CollectionUtils.isEmpty(preNacosRegisters)) {
            afterPropertiesSet();
        } else {
            Map<String, NacosRegister> preRegister = new HashMap<>(8);
            Map<String, NacosRegister> nowRegister = new HashMap<>(8);
            //3个for循环解法时间复杂度为O（n）（忽略常数倍），比双重for的O（n^2）优
            for (NacosRegister preNacosRegister : preNacosRegisters) {
                preRegister.put(preNacosRegister.getRegisterId(), preNacosRegister);
            }
            List<AbstractSecondSchedule> secondSchedules = getSecondSchedules();
            for (NacosRegister register : discoveryProperties.getRegister()) {
                NacosRegister pre = preRegister.get(register.getRegisterId());
                if (pre == null) {
                    //进入此判定说明新增了
                    Properties properties = getProperties(register);
                    naming.put(register.getRegisterId(), NacosFactory.createNamingService(properties));
                } else if (!pre.equals(register)) {
                    //进入此处判定说明相同registerId发生了修改，要对老的进行替换
                    NamingService namingService = naming.get(register.getRegisterId());
                    Properties properties = getProperties(register);
                    naming.put(register.getRegisterId(), NacosFactory.createNamingService(properties));
                    //shutDownNaming前，将定时任务结束
                    for (AbstractSecondSchedule secondSchedule : secondSchedules) {
                        if (secondSchedule.getRegisterId().equals(register.getRegisterId())) {
                            delSecondSchedules(secondSchedule);
                        }
                    }
                    namingService.shutDown();
                }
                nowRegister.put(register.getRegisterId(), register);
            }
            //再进行一次之前的集合遍历，筛选出被删除的
            for (NacosRegister preNacosRegister : preNacosRegisters) {
                NacosRegister register = nowRegister.get(preNacosRegister.getRegisterId());
                //说明在新的这波被删除了
                if (register == null) {
                    //shutDownNaming前，将定时任务结束
                    for (AbstractSecondSchedule secondSchedule : secondSchedules) {
                        if (secondSchedule.getRegisterId().equals(preNacosRegister.getRegisterId())) {
                            delSecondSchedules(secondSchedule);
                        }
                    }
                    NamingService remove = naming.remove(preNacosRegister.getRegisterId());
                    if (remove != null) {
                        remove.shutDown();
                    }
                }
            }
            preNacosRegisters = new ArrayList<>(discoveryProperties.getRegister());
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.context = event.getApplicationContext();
    }
}
