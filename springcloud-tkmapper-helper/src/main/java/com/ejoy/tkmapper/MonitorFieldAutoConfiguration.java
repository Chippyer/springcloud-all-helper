package com.ejoy.tkmapper;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class, RedissonAutoConfiguration.class})
public class MonitorFieldAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisMonitorService monitorService() {
        return new RedisMonitorService();
    }

    @Bean
    public OperationLogDefinitionResolver operationLogDefinitionResolver() {
        return new OperationLogDefinitionResolver();
    }

    @Bean
    public MonitorDataExecutorListener monitorDataExecutorListener(StringRedisTemplate stringRedisTemplate) {
        return new MonitorDataExecutorListener(stringRedisTemplate);
    }

}
