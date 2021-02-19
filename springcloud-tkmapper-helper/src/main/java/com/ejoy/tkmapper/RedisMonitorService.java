package com.ejoy.tkmapper;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ReflectUtil;
import com.ejoy.core.common.utils.ObjectsUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Objects;

/**
 * Redis监控服务实现
 *
 * @author: chippy
 * @datetime 2021-02-19 17:10
 */
@Slf4j
public class RedisMonitorService implements MonitorService, ApplicationEventPublisherAware {

    private RLiveObjectService liveObjectService;
    private ApplicationEventPublisher applicationEventPublisher;

    public RedisMonitorService(RLiveObjectService liveObjectService) {
        this.liveObjectService = liveObjectService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void update(Object object) {
        final String classFullPath = object.getClass().getName();
        final String primaryKeyField = MonitorClassDefinition.get(classFullPath);
        String id = classFullPath + primaryKeyField;
        final OperationLogInfo operationLogInfo = liveObjectService.get(OperationLogInfo.class, id);
        if (ObjectsUtil.isEmpty(operationLogInfo)) {
            return;
        }
        final Object monitorFieldValue = ReflectUtil.getFieldValue(object, operationLogInfo.getMonitorField());
        if (Objects.isNull(operationLogInfo.getMonitorFieldValue())) {
            final OperationLogInfo updateOperationLogInfo = new OperationLogInfo();
            BeanUtils.copyProperties(operationLogInfo, updateOperationLogInfo);
            updateOperationLogInfo.setMonitorFieldValue(String.valueOf(monitorFieldValue));
            updateOperationLogInfo.setUpdateDateTime(new DateTime().toStringDefaultTimeZone());
            liveObjectService.merge(updateOperationLogInfo);
        }
        if (!Objects.equals(operationLogInfo.getMonitorFieldValue(), monitorFieldValue)) {
            final OperationLogInfo updateOperationLogInfo = new OperationLogInfo();
            BeanUtils.copyProperties(operationLogInfo, updateOperationLogInfo);
            updateOperationLogInfo.setMonitorFieldValue(String.valueOf(monitorFieldValue));
            updateOperationLogInfo.setUpdateDateTime(new DateTime().toStringDefaultTimeZone());
            liveObjectService.merge(updateOperationLogInfo);
            //            applicationEventPublisher.publishEvent(new MethodMonitorEvent(operationLogInfo));
        }
    }

}
