package com.ejoy.tkmapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * 方法监控事件定义
 *
 * @author: chippy
 */
@Slf4j
public class MonitorEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MonitorEvent(Object source) {
        super(source);
    }

}
