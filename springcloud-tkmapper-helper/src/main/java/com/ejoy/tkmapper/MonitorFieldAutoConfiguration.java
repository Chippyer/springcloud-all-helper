package com.ejoy.tkmapper;

import org.redisson.api.RLiveObjectService;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class, RedissonAutoConfiguration.class})
public class MonitorFieldAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisMonitorService monitorService(RLiveObjectService liveObjectService) {
        return new RedisMonitorService(liveObjectService);
    }

    @Bean
    public OperationLogDefinitionResolver operationLogDefinitionResolver(RLiveObjectService liveObjectService) {
        return new OperationLogDefinitionResolver(liveObjectService);
    }
}
