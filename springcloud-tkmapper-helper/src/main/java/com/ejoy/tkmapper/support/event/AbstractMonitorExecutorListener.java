package com.ejoy.tkmapper.support.event;

import com.ejoy.tkmapper.support.domain.MonitorOperationLogInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 抽象监控事件监听者，提供辅助方法实现
 *
 * @author: chippy
 * @datetime 2021-02-20 14:55
 */
@Slf4j
@SuppressWarnings("all")
public class AbstractMonitorExecutorListener {

    public Map<String, MonitorOperationLogInfo> getSourceInfo(MonitorEvent monitorEvent) {
        return (Map<String, MonitorOperationLogInfo>)monitorEvent.getSource();
    }

}
