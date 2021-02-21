package com.ejoy.tkmapper.configuration;

import com.ejoy.tkmapper.support.api.IMonitorService;
import com.ejoy.tkmapper.support.api.MonitorService;
import com.ejoy.tkmapper.support.aspect.AutoMonitorExecutor;
import com.ejoy.tkmapper.support.definition.MonitorDefinitionResolver;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class, RedissonAutoConfiguration.class})
public class MonitorFieldAutoConfiguration {

    @Bean
    public MonitorService monitorService(StringRedisTemplate stringRedisTemplate) {
        return new MonitorService(stringRedisTemplate);
    }

    @Bean
    public MonitorDefinitionResolver monitorDefinitionResolver() {
        return new MonitorDefinitionResolver();
    }

    @Bean
    public AutoMonitorExecutor autoMonitorExecutor(IMonitorService monitorService) {
        return new AutoMonitorExecutor(monitorService);
    }

}
