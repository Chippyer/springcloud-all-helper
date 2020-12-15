package com.chippy.redis.redisson.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.chippy.redis.redisson.enums.RedisConnectionTypeEnum;
import com.chippy.redis.redisson.exception.NotSupportedConnTypeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.*;
import org.springframework.context.ApplicationContext;

/**
 * Redisson核心配置，用于提供初始化的redisson实例。
 *
 * @author chippy
 */
@Slf4j
public class RedissonManager {

    @Getter
    private Redisson redisson;

    private ApplicationContext applicationContext;

    public RedissonManager(String connectionType, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        try {
            Config config = this.doGetConfig(connectionType);
            redisson = (Redisson)Redisson.create(config);
            log.debug("Redisson-[" + connectionType + "]-初始化完成-初始化配置信息-" + JSONUtil.toJsonStr(config));
        } catch (NotSupportedConnTypeException ex) {
            log.error("Redisson-[" + connectionType + "]-初始化异常: " + ex.getMessage());
        }
    }

    private Config doGetConfig(String connectionType) {
        Config config = new Config();
        final Object bean = applicationContext.getBean(connectionType);
        if (connectionType.equals(RedisConnectionTypeEnum.STANDALONE.getConnectionType())) {
            final SingleServerConfig singleServerConfig = config.useSingleServer();
            SingleServerConfig definitionBean = (SingleServerConfig)bean;
            BeanUtil.copyProperties(definitionBean, singleServerConfig);
        } else if (connectionType.equals(RedisConnectionTypeEnum.SENTINEL.getConnectionType())) {
            final SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
            SentinelServersConfig definitionBean = (SentinelServersConfig)bean;
            BeanUtil.copyProperties(definitionBean, sentinelServersConfig);
        } else if (connectionType.equals(RedisConnectionTypeEnum.CLUSTER.getConnectionType())) {
            final ClusterServersConfig clusterServersConfig = config.useClusterServers();
            ClusterServersConfig definitionBean = (ClusterServersConfig)bean;
            BeanUtil.copyProperties(definitionBean, clusterServersConfig);
        } else if (connectionType.equals(RedisConnectionTypeEnum.MASTER_SLAVE.getConnectionType())) {
            final MasterSlaveServersConfig masterSlaveServersConfig = config.useMasterSlaveServers();
            MasterSlaveServersConfig definitionBean = (MasterSlaveServersConfig)bean;
            BeanUtil.copyProperties(definitionBean, masterSlaveServersConfig);
        } else {
            throw new NotSupportedConnTypeException("不支持的Redisson创建类型-" + connectionType);
        }
        return config;
    }

}