package com.ejoy.tkmapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Redis监控服务实现
 *
 * @author: chippy
 * @datetime 2021-02-19 17:10
 */
@Slf4j
public class RedisMonitorService implements MonitorService, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void update(Object object) {
        applicationEventPublisher.publishEvent(new MonitorEvent(object));
    }

}
