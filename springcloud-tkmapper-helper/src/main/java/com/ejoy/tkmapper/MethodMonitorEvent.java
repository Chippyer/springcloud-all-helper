package com.ejoy.tkmapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * 方法监控事件定义
 *
 * @author: chippy
 */
@Slf4j
public class MethodMonitorEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MethodMonitorEvent(Object source) {
        super(source);
        OperationLogInfo operationLogInfo = (OperationLogInfo)source;
        if (log.isDebugEnabled()) {
            log.debug("方法监控事件发送，数据唯一标识z值[" + operationLogInfo.getId() + "], 业务唯一标识值[" + operationLogInfo
                .getMonitorPrimaryKeyFieldValue() + "]");
        }
    }

}
