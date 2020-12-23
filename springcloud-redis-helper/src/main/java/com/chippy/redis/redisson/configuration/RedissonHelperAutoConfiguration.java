package com.chippy.redis.redisson.configuration;

import com.chippy.redis.redisson.task.support.ScheduledTaskDefinitionResolver;
import com.ulisesbocchio.jasyptspringboot.annotation.ConditionalOnMissingBean;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 *
 * @author: chippy
 * @datetime 2020-12-16 16:12
 */
@Configuration
@ComponentScan({"com.chippy.redis.redisson.task.support"})
@AutoConfigureAfter(RedissonAutoConfiguration.class)
public class RedissonHelperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RLiveObjectService liveObjectService(RedissonClient redissonClient) {
        return redissonClient.getLiveObjectService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledTaskDefinitionResolver scheduledTaskDefinitionResolver() {
        return new ScheduledTaskDefinitionResolver();
    }

}
