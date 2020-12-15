package com.chippy.redis.redisson.configuration;

import com.chippy.redis.redisson.exception.RedissonManagerCreateException;
import com.chippy.redis.redisson.support.RedissonManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.*;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * Redisson自动化配置
 *
 * @author chippy
 */
@Slf4j
@Configuration
@ConditionalOnClass(Redisson.class)
@EnableConfigurationProperties
public class RedissonAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "redisson.cluster")
    public ClusterServersConfig clusterServersConfig() {
        return new ClusterServersConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "redisson.master-slave")
    public MasterSlaveServersConfig masterSlaveServersConfig() {
        return new MasterSlaveServersConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "redisson.sentinel")
    public SentinelServersConfig sentinelServersConfig() {
        return new SentinelServersConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "redisson.single")
    public SingleServerConfig singleServerConfig() {
        final Config config = new Config();
        return config.useSingleServer();
    }

    @Bean
    @ConditionalOnMissingBean
    @Order(value = 1)
    public RedissonManager redissonManager() {
        final Environment environment = applicationContext.getEnvironment();
        final String type = environment.getProperty("redisson.type");
        if (null == type) {
            throw new RedissonManagerCreateException(
                "配置文件中必须配置-[redisson.type], 可供选择值-[singleServerConfig, sentinelServersConfig, clusterServersConfig, masterSlaveServersConfig]");
        }
        return new RedissonManager(type, applicationContext);
    }

}

