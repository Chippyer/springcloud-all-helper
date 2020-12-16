//package com.chippy.redis.redisson.support;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.json.JSONUtil;
//import com.chippy.redis.redisson.configuration.RedissonProperties;
//import com.chippy.redis.redisson.enums.RedisConnectionTypeEnum;
//import com.chippy.redis.redisson.exception.NotSupportedConnTypeException;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.Redisson;
//import org.redisson.config.*;
//
///**
// * Redisson核心配置，用于提供初始化的redisson实例。
// *
// * @author chippy
// */
//@Slf4j
//public class RedissonManager {
//
//    @Getter
//    private Redisson redisson;
//
//    public RedissonManager(String connectionType, RedissonProperties redissonProperties) {
//        try {
//            Config config = this.doGetConfig(connectionType, redissonProperties);
//            redisson = (Redisson)Redisson.create(config);
//            log.debug("Redisson-[" + connectionType + "]-初始化完成-初始化配置信息-" + JSONUtil.toJsonStr(config));
//        } catch (NotSupportedConnTypeException ex) {
//            log.error("Redisson-[" + connectionType + "]-初始化异常: " + ex.getMessage());
//        }
//    }
//
//    private Config doGetConfig(String connectionType, RedissonProperties redissonProperties) {
//        Config config = new Config();
//        if (connectionType.equals(RedisConnectionTypeEnum.STANDALONE.getConnectionType())) {
//            final SingleServerConfig singleServerConfig = config.useSingleServer();
//            BeanUtil.copyProperties(redissonProperties, singleServerConfig);
//            singleServerConfig.setPassword(redissonProperties.getPassword());
//        } else if (connectionType.equals(RedisConnectionTypeEnum.SENTINEL.getConnectionType())) {
//            final SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
//            BeanUtil.copyProperties(redissonProperties, sentinelServersConfig);
//            sentinelServersConfig.setPassword(redissonProperties.getPassword());
//        } else if (connectionType.equals(RedisConnectionTypeEnum.CLUSTER.getConnectionType())) {
//            final ClusterServersConfig clusterServersConfig = config.useClusterServers();
//            BeanUtil.copyProperties(redissonProperties, clusterServersConfig);
//            clusterServersConfig.setPassword(redissonProperties.getPassword());
//        } else if (connectionType.equals(RedisConnectionTypeEnum.MASTER_SLAVE.getConnectionType())) {
//            final MasterSlaveServersConfig masterSlaveServersConfig = config.useMasterSlaveServers();
//            BeanUtil.copyProperties(redissonProperties, masterSlaveServersConfig);
//            masterSlaveServersConfig.setPassword(redissonProperties.getPassword());
//        } else {
//            throw new NotSupportedConnTypeException("不支持的链接类型[" + connectionType + "]");
//        }
//        return config;
//    }
//
//}