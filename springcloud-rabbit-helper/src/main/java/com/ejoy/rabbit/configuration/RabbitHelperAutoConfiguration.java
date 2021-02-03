package com.ejoy.rabbit.configuration;

import com.ejoy.rabbit.support.MemoryRabbitMQOperationService;
import com.ejoy.rabbit.support.RabbitMQOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * RabbitMQ配置类
 *
 * @author: chippy
 * @datetime 2021-02-02 15:05
 */
@Configurable
@EnableConfigurationProperties(RabbitHelperProperties.class)
@Slf4j
public class RabbitHelperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = RabbitMQOperationService.class)
    private MemoryRabbitMQOperationService memoryRabbitMQOperationService() {
        return new MemoryRabbitMQOperationService();
    }

}
