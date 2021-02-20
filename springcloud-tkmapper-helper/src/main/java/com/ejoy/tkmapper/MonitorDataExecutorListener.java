package com.ejoy.tkmapper;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.core.common.utils.ObjectsUtil;
import com.ejoy.core.common.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;
import java.util.Set;

/**
 * 监控事件{@link MonitorEvent}监听者之一
 * 监听到事件数据后会通过{@link MonitorExecutor}制定数据处理器对数据进行变更内容更新记录
 *
 * @author: chippy
 * @datetime 2021-02-20 14:55
 */
@Slf4j
public class MonitorDataExecutorListener {

    private StringRedisTemplate stringRedisTemplate;

    public MonitorDataExecutorListener(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @SuppressWarnings("unchecked")
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void processListenerEvent(MonitorEvent monitorEvent) {
        final Object source = monitorEvent.getSource();
        if (ObjectsUtil.isNotEmpty(source)) {
            final String classFullPath = source.getClass().getName();
            final MonitorDefinition monitorDefinition = MonitorClassDefinition.get(classFullPath);
            if (Objects.isNull(monitorDefinition)) {
                return;
            }
            String monitorField = monitorDefinition.getMonitorField(), primaryKeyField =
                monitorDefinition.getPrimaryKeyField();
            final Object monitorFieldValue = ReflectUtil.getFieldValue(source, monitorField);
            final String primaryKeyFieldValue = String.valueOf(ReflectUtil.getFieldValue(source, primaryKeyField));
            final String key = classFullPath + "_" + primaryKeyFieldValue;
            final String desc = "业务数据唯一标识[" + primaryKeyFieldValue + "], 更新后[" + monitorFieldValue + "]";

            final Set<Object> hashKeys = stringRedisTemplate.opsForHash().keys(key);
            if (CollectionsUtils.isEmpty(hashKeys) || hashKeys.size() == 0) {
                this.insert(key, monitorField, primaryKeyField, monitorFieldValue, primaryKeyFieldValue, desc);
                return;
            }
            String lastMonitorOperationLogInfoJson =
                String.valueOf(stringRedisTemplate.opsForHash().multiGet(key, hashKeys).get(hashKeys.size() - 1));
            if (Objects.isNull(lastMonitorOperationLogInfoJson)) {
                this.insert(key, monitorField, primaryKeyField, monitorFieldValue, primaryKeyFieldValue, desc);
                return;
            }
            final MonitorOperationLogInfo lastMonitorOperationLogInfo =
                JSONUtil.toBean(lastMonitorOperationLogInfoJson, MonitorOperationLogInfo.class);
            if (!Objects
                .equals(lastMonitorOperationLogInfo.getMonitorFieldValue(), String.valueOf(monitorFieldValue))) {
                this.insert(key, monitorField, primaryKeyField, monitorFieldValue, primaryKeyFieldValue, desc);
            }
        }
    }

    private String insert(String key, String monitorField, String primaryKeyField, Object monitorFieldValue,
        Object primaryKeyFieldValue, String desc) {
        final String recordUid = UUIDUtil.generateUuid();
        MonitorOperationLogInfo monitorOperationLogInfo =
            this.doInsert(monitorField, primaryKeyField, monitorFieldValue, primaryKeyFieldValue, desc);
        stringRedisTemplate.opsForHash().put(key, UUIDUtil.generateUuid(), JSONUtil.toJsonStr(monitorOperationLogInfo));
        return recordUid;
    }

    private MonitorOperationLogInfo doInsert(String monitorField, String primaryKeyField, Object monitorFieldValue,
        Object primaryKeyFieldValue, String desc) {
        MonitorOperationLogInfo monitorOperationLogInfo = new MonitorOperationLogInfo();
        monitorOperationLogInfo.setMonitorField(monitorField);
        monitorOperationLogInfo.setMonitorFieldValue(String.valueOf(monitorFieldValue));
        monitorOperationLogInfo.setPrimaryKeyField(primaryKeyField);
        monitorOperationLogInfo.setPrimaryKeyFieldValue(String.valueOf(primaryKeyFieldValue));
        monitorOperationLogInfo.setCreateDateTime(new DateTime().toStringDefaultTimeZone());
        monitorOperationLogInfo.setDesc(desc);
        return monitorOperationLogInfo;
    }

}
