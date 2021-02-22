package com.ejoy.tkmapper.support.api;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.ejoy.core.common.constants.GlobalConstantEnum;
import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.tkmapper.support.definition.MonitorClassDefinition;
import com.ejoy.tkmapper.support.domain.MonitorOperationLogInfo;
import com.ejoy.tkmapper.support.event.MonitorEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 监控服务类实现
 *
 * @author: chippy
 * @datetime 2021-02-19 17:10
 */
@Slf4j
@SuppressWarnings("all")
public class MonitorService
    implements IMonitorService, ApplicationContextAware, ApplicationEventPublisherAware, InitializingBean {

    private static final int DEFAULT_START_INDEX = 0;
    private static final int ONE = 1;
    private static final int DEFAULT_END_INDEX = 10;

    private long commonExpireTime = 604800000;
    private StringRedisTemplate stringRedisTemplate;
    private ApplicationContext applicationContext;
    private ApplicationEventPublisher applicationEventPublisher;

    public MonitorService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void process(Object object) {
        this.process(object, null);
    }

    @Override
    public void process(Object monitorObject, String extensionParam) {
        if (Objects.isNull(monitorObject)) {
            if (log.isTraceEnabled()) {
                log.trace("传入对象数据为空");
            }
            return;
        }
        final MonitorClassDefinition.Element element = MonitorClassDefinition.get(monitorObject.getClass().getName());
        if (Objects.isNull(element)) {
            if (log.isTraceEnabled()) {
                log.trace("传入对象未标识为监控对象");
            }
            return;
        }
        final String primaryKeyFieldValue =
            String.valueOf(ReflectUtil.getFieldValue(monitorObject, element.getPrimaryKeyField()));
        if (Objects.isNull(primaryKeyFieldValue)) {
            if (log.isTraceEnabled()) {
                log.trace("传入对象主键值为空");
            }
            return;
        }
        final List<String> monitorFields = element.getMonitorFields();
        final List<String> monitorFieldValues = this.doResolveMonitorFields(monitorFields, monitorObject);
        if (monitorFields.size() != monitorFieldValues.size()) {
            return;
        }

        Map<String, MonitorOperationLogInfo> changeMonitorFieldMap = new HashMap<>();
        int monitorFieldSize = monitorFields.size();
        for (int i = 0; i < monitorFieldSize; i++) {
            final String monitorField = monitorFields.get(i), monitorFieldValue = monitorFieldValues.get(i);
            final List<String> lastMonitorOperationLogInfoJsonList = stringRedisTemplate.opsForList()
                .range(this.doGetKey(element.getMonitorClassFullPath(), monitorField, primaryKeyFieldValue), 0, 1);
            if (CollectionUtils.isEmpty(lastMonitorOperationLogInfoJsonList)) {
                final MonitorOperationLogInfo monitorOperationLogInfo =
                    this.doInsert(element, monitorField, monitorFieldValue, primaryKeyFieldValue, extensionParam);
                changeMonitorFieldMap.put(monitorField, monitorOperationLogInfo);
                continue;
            }
            final String lastMonitorOperationLogInfoJson = lastMonitorOperationLogInfoJsonList.get(0);
            if (Objects.isNull(lastMonitorOperationLogInfoJson)) {
                final MonitorOperationLogInfo monitorOperationLogInfo =
                    this.doInsert(element, monitorField, monitorFieldValue, primaryKeyFieldValue, extensionParam);
                changeMonitorFieldMap.put(monitorField, monitorOperationLogInfo);
                continue;
            }
            final MonitorOperationLogInfo lastMonitorOperationLogInfo =
                JSONUtil.toBean(lastMonitorOperationLogInfoJson, MonitorOperationLogInfo.class);
            if (Objects.equals(lastMonitorOperationLogInfo.getMonitorFieldValue(), monitorFieldValue)) {
                if (log.isTraceEnabled()) {
                    log.trace("此次变更值[" + monitorFieldValue + "]最后一次变更后的值[" + lastMonitorOperationLogInfo
                        .getMonitorFieldValue() + "]相同");
                }
                continue;
            }
            final MonitorOperationLogInfo monitorOperationLogInfo =
                this.doInsert(element, monitorField, monitorFieldValue, primaryKeyFieldValue, extensionParam);
            changeMonitorFieldMap.put(monitorField, monitorOperationLogInfo);
        }

        if (element.getCustomerProcess()) {
            // 自定义操作 -> 发送监控事件
            if (log.isTraceEnabled()) {
                log.trace("自定义处理字段操作记录");
            }
            if (changeMonitorFieldMap.entrySet().size() > 0) {
                applicationEventPublisher.publishEvent(new MonitorEvent(changeMonitorFieldMap));
            }
        }
    }

    @Override
    public MonitorOperationLogInfo getOne(Class clazz, String id, String monitorField) {
        if (Objects.isNull(clazz) || Objects.isNull(id)) {
            return null;
        }
        final List<MonitorOperationLogInfo> monitorOperationLogInfos =
            this.get(clazz, id, monitorField, DEFAULT_START_INDEX, ONE);
        if (CollectionUtils.isEmpty(monitorOperationLogInfos)) {
            return null;
        }
        return monitorOperationLogInfos.get(0);
    }

    @Override
    public List<MonitorOperationLogInfo> get(Class clazz, String id, String monitorField) {
        if (Objects.isNull(clazz) || Objects.isNull(id)) {
            return Collections.emptyList();
        }
        return this.get(clazz, id, monitorField, DEFAULT_START_INDEX, DEFAULT_END_INDEX);
    }

    @Override
    public List<MonitorOperationLogInfo> get(Class clazz, String id, String monitorField, int endIndex) {
        if (Objects.isNull(clazz) || Objects.isNull(id)) {
            return Collections.emptyList();
        }
        return this.get(clazz, id, monitorField, DEFAULT_START_INDEX, endIndex);
    }

    @Override
    public List<MonitorOperationLogInfo> get(Class clazz, String id, String monitorField, int startIndex,
        int endIndex) {
        final List<String> monitorOperationLogInfoJsonList = stringRedisTemplate.opsForList()
            .range(this.doGetKey(clazz.getName(), monitorField, id), startIndex, endIndex);
        if (CollectionsUtils.isEmpty(monitorOperationLogInfoJsonList)) {
            return Collections.emptyList();
        }
        return monitorOperationLogInfoJsonList.stream().map(
            (Function<String, MonitorOperationLogInfo>)monitorOperationLogInfoJson -> JSONUtil
                .toBean(monitorOperationLogInfoJson, MonitorOperationLogInfo.class, true)).filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private String doGetKey(String monitorClassFullPath, String monitorField, String id) {
        return monitorClassFullPath + GlobalConstantEnum.COLON.getConstantValue() + id + GlobalConstantEnum.COLON
            .getConstantValue() + monitorField;
    }

    private List<String> doResolveMonitorFields(List<String> monitorFields, Object monitorObject) {
        if (CollectionsUtils.isEmpty(monitorFields)) {
            return Collections.emptyList();
        }
        return monitorFields.stream()
            .map(monitorField -> String.valueOf(ReflectUtil.getFieldValue(monitorObject, monitorField)))
            .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private MonitorOperationLogInfo doInsert(MonitorClassDefinition.Element element, String monitorField,
        String monitorFieldValue, String primaryKeyFieldValue, String extendParam) {
        final String key = this.doGetKey(element.getMonitorClassFullPath(), monitorField, primaryKeyFieldValue);
        final String primaryKeyField = element.getPrimaryKeyField();
        final long expireTime = element.getExpireTime() == 0 ? commonExpireTime : element.getExpireTime();
        MonitorOperationLogInfo monitorOperationLogInfo = new MonitorOperationLogInfo();
        monitorOperationLogInfo.setMonitorField(monitorField);
        monitorOperationLogInfo.setMonitorFieldValue(monitorFieldValue);
        monitorOperationLogInfo.setPrimaryKeyField(primaryKeyField);
        monitorOperationLogInfo.setPrimaryKeyFieldValue(primaryKeyFieldValue);
        monitorOperationLogInfo.setCreateDateTime(new DateTime());
        monitorOperationLogInfo.setDesc(this.doGetDesc(monitorFieldValue, primaryKeyFieldValue));
        monitorOperationLogInfo.setExtensionParam(extendParam);
        stringRedisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(monitorOperationLogInfo));
        stringRedisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
        return monitorOperationLogInfo;
    }

    private String doGetDesc(Object monitorFieldValue, String primaryKeyFieldValue) {
        return "业务数据唯一标识[" + primaryKeyFieldValue + "], 更新后[" + monitorFieldValue + "]";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.commonExpireTime = Long.parseLong(Objects.requireNonNull(this.applicationContext.getEnvironment()
            .getProperty(GlobalConstantEnum.MONITOR_COMMON_EXPIRE.getConstantValue())));
    }
}
