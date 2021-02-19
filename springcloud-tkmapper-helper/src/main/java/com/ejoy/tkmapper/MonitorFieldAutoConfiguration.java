package com.ejoy.tkmapper;

import org.redisson.api.RLiveObjectService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
