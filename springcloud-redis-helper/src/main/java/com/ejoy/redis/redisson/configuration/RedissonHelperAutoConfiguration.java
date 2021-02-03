package com.ejoy.redis.redisson.configuration;

import com.ejoy.redis.redisson.task.support.ScheduledTaskDefinitionResolver;
import com.ejoy.redis.redisson.task.support.SpringScheduler;
import com.ejoy.redis.support.api.*;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Redisson配置类
 *
 * @author: chippy
 * @datetime 2020-12-16 16:12
 */
@Configuration
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

    @Bean
    @ConditionalOnMissingBean
    public SpringScheduler springScheduler(RLiveObjectService liveObjectService) {
        return new SpringScheduler(liveObjectService);
    }

    // ====================== 内置XXXTemplate ======================
    @Bean
    @ConditionalOnMissingBean
    public BigDecimalRedisTemplate bigDecimalRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new BigDecimalRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public BooleanRedisTemplate booleanRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new BooleanRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public DoubleRedisTemplate doubleRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new DoubleRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public FloatRedisTemplate floatRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new FloatRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public IntegerRedisTemplate integerRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new IntegerRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public LongRedisTemplate longRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new LongRedisTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShortRedisTemplate shortRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new ShortRedisTemplate(connectionFactory);
    }

}
