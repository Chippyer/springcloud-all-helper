package com.ejoy.tkmapper.support.event;

import com.ejoy.tkmapper.support.domain.MonitorOperationLogInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象监控事件监听者，提供辅助方法实现
 *
 * @author: chippy
 * @datetime 2021-02-20 14:55
 */
@Slf4j
public class AbstractMonitorExecutorListener {

    public MonitorOperationLogInfo getSourceInfo(MonitorEvent monitorEvent) {
        return (MonitorOperationLogInfo)monitorEvent.getSource();
    }

}
