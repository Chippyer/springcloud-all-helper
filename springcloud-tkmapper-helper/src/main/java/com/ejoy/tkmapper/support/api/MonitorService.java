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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    public void process(Object object, String customerDesc) {
        if (Objects.isNull(object)) {
            if (log.isTraceEnabled()) {
                log.trace("传入对象数据为空");
            }
            return;
        }
        final String classFullPath = object.getClass().getName();
        final MonitorClassDefinition.Element element = MonitorClassDefinition.get(classFullPath);
        if (Objects.isNull(element)) {
            if (log.isTraceEnabled()) {
                log.trace("传入对象未标识为监控对象");
            }
            return;
        }
        final String monitorFieldValue = String.valueOf(ReflectUtil.getFieldValue(object, element.getMonitorField()));
        final String primaryKeyFieldValue =
            String.valueOf(ReflectUtil.getFieldValue(object, element.getPrimaryKeyField()));
        if (Objects.isNull(primaryKeyFieldValue)) {
            if (log.isTraceEnabled()) {
                log.trace("传入对象主键值为空");
            }
            return;
        }
        final List<String> lastMonitorOperationLogInfoJsonList = stringRedisTemplate.opsForList()
            .range(this.doGetKey(element.getMonitorClassFullPath(), primaryKeyFieldValue), 0, 1);
        if (CollectionUtils.isEmpty(lastMonitorOperationLogInfoJsonList)) {
            this.doProcess(element, monitorFieldValue, primaryKeyFieldValue, customerDesc);
            return;
        }
        final String lastMonitorOperationLogInfoJson = lastMonitorOperationLogInfoJsonList.get(0);
        if (Objects.isNull(lastMonitorOperationLogInfoJson)) {
            this.doProcess(element, monitorFieldValue, primaryKeyFieldValue, customerDesc);
            return;
        }
        final MonitorOperationLogInfo lastMonitorOperationLogInfo =
            JSONUtil.toBean(lastMonitorOperationLogInfoJson, MonitorOperationLogInfo.class);
        if (Objects.equals(lastMonitorOperationLogInfo.getMonitorFieldValue(), String.valueOf(monitorFieldValue))) {
            if (log.isTraceEnabled()) {
                log.trace(
                    "此次变更值[" + monitorFieldValue + "]最后一次变更后的值[" + lastMonitorOperationLogInfo.getMonitorFieldValue()
                        + "]相同");
            }
            return;
        }
        this.doProcess(element, monitorFieldValue, primaryKeyFieldValue, customerDesc);
    }

    @Override
    public MonitorOperationLogInfo getOne(Class clazz, String id) {
        if (Objects.isNull(clazz) || Objects.isNull(id)) {
            return null;
        }
        final List<MonitorOperationLogInfo> monitorOperationLogInfos = this.get(clazz, id, DEFAULT_START_INDEX, ONE);
        if (CollectionUtils.isEmpty(monitorOperationLogInfos)) {
            return null;
        }
        return monitorOperationLogInfos.get(0);
    }

    @Override
    public List<MonitorOperationLogInfo> get(Class clazz, String id) {
        if (Objects.isNull(clazz) || Objects.isNull(id)) {
            return Collections.emptyList();
        }
        return this.get(clazz, id, DEFAULT_START_INDEX, DEFAULT_END_INDEX);
    }

    @Override
    public List<MonitorOperationLogInfo> get(Class clazz, String id, int endIndex) {
        if (Objects.isNull(clazz) || Objects.isNull(id)) {
            return Collections.emptyList();
        }
        return this.get(clazz, id, DEFAULT_START_INDEX, endIndex);
    }

    @Override
    public List<MonitorOperationLogInfo> get(Class clazz, String id, int startIndex, int endIndex) {
        final List<String> monitorOperationLogInfoJsonList =
            stringRedisTemplate.opsForList().range(this.doGetKey(clazz.getName(), id), startIndex, endIndex);
        if (CollectionsUtils.isEmpty(monitorOperationLogInfoJsonList)) {
            return Collections.emptyList();
        }
        return monitorOperationLogInfoJsonList.stream().map(
            (Function<String, MonitorOperationLogInfo>)monitorOperationLogInfoJson -> JSONUtil
                .toBean(monitorOperationLogInfoJson, MonitorOperationLogInfo.class, true)).filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private void doProcess(MonitorClassDefinition.Element element, String monitorFieldValue,
        String primaryKeyFieldValue, String desc) {
        final String finalDesc = Objects.isNull(desc) ? this.doGetDesc(monitorFieldValue, primaryKeyFieldValue) : desc;
        this.doInsert(element, monitorFieldValue, primaryKeyFieldValue, finalDesc);
        if (element.getCustomerProcess()) {
            // 自定义操作 -> 发送监控事件
            if (log.isTraceEnabled()) {
                log.trace("自定义处理字段操作记录");
            }
            applicationEventPublisher.publishEvent(new MonitorEvent(
                this.doBuildMonitorOpreationLogInfo(element, monitorFieldValue, primaryKeyFieldValue, finalDesc)));
        }
    }

    private String doGetKey(String classFullPath, String id) {
        return classFullPath + GlobalConstantEnum.COLON.getConstantValue() + id;
    }

    private String doGetDesc(Object monitorFieldValue, String primaryKeyFieldValue) {
        return "业务数据唯一标识[" + primaryKeyFieldValue + "], 更新后[" + monitorFieldValue + "]";
    }

    private void doInsert(String key, String monitorField, String primaryKeyField, String monitorFieldValue,
        String primaryKeyFieldValue, String desc) {
        MonitorOperationLogInfo monitorOperationLogInfo = new MonitorOperationLogInfo();
        monitorOperationLogInfo.setMonitorField(monitorField);
        monitorOperationLogInfo.setMonitorFieldValue(String.valueOf(monitorFieldValue));
        monitorOperationLogInfo.setPrimaryKeyField(primaryKeyField);
        monitorOperationLogInfo.setPrimaryKeyFieldValue(String.valueOf(primaryKeyFieldValue));
        monitorOperationLogInfo.setCreateDateTime(new DateTime());
        monitorOperationLogInfo.setDesc(desc);
        stringRedisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(
            this.doBuildMonitorOpreationLogInfo(monitorField, primaryKeyField, monitorFieldValue, primaryKeyFieldValue,
                desc)));
    }

    private void doInsert(MonitorClassDefinition.Element element, String monitorFieldValue, String primaryKeyFieldValue,
        String desc) {
        final String key = this.doGetKey(element.getMonitorClassFullPath(), primaryKeyFieldValue);
        final String monitorField = element.getMonitorField(), primaryKeyField = element.getPrimaryKeyField();
        final long expireTime = element.getExpireTime() == 0 ? commonExpireTime : element.getExpireTime();
        MonitorOperationLogInfo monitorOperationLogInfo = new MonitorOperationLogInfo();
        monitorOperationLogInfo.setMonitorField(monitorField);
        monitorOperationLogInfo.setMonitorFieldValue(monitorFieldValue);
        monitorOperationLogInfo.setPrimaryKeyField(primaryKeyField);
        monitorOperationLogInfo.setPrimaryKeyFieldValue(primaryKeyFieldValue);
        monitorOperationLogInfo.setCreateDateTime(new DateTime());
        monitorOperationLogInfo.setDesc(desc);
        stringRedisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(
            this.doBuildMonitorOpreationLogInfo(monitorField, primaryKeyField, monitorFieldValue, primaryKeyFieldValue,
                desc)));
        stringRedisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
    }

    private MonitorOperationLogInfo doBuildMonitorOpreationLogInfo(MonitorClassDefinition.Element element,
        String monitorFieldValue, String primaryKeyFieldValue, String desc) {
        return this
            .doBuildMonitorOpreationLogInfo(element.getMonitorField(), element.getPrimaryKeyField(), monitorFieldValue,
                primaryKeyFieldValue, desc);
    }

    private MonitorOperationLogInfo doBuildMonitorOpreationLogInfo(String monitorField, String primaryKeyField,
        String monitorFieldValue, String primaryKeyFieldValue, String desc) {
        MonitorOperationLogInfo monitorOperationLogInfo = new MonitorOperationLogInfo();
        monitorOperationLogInfo.setMonitorField(monitorField);
        monitorOperationLogInfo.setMonitorFieldValue(monitorFieldValue);
        monitorOperationLogInfo.setPrimaryKeyField(primaryKeyField);
        monitorOperationLogInfo.setPrimaryKeyFieldValue(primaryKeyFieldValue);
        monitorOperationLogInfo.setCreateDateTime(new DateTime());
        monitorOperationLogInfo.setDesc(desc);
        return monitorOperationLogInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.commonExpireTime = Long.parseLong(Objects.requireNonNull(this.applicationContext.getEnvironment()
            .getProperty(GlobalConstantEnum.MONITOR_COMMON_EXPIRE.getConstantValue())));
    }
}
