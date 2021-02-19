//package com.ejoy.tkmapper;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.date.DateTime;
//import com.ejoy.core.common.utils.CommonSpringContext;
//import com.ejoy.core.common.utils.ObjectsUtil;
//import com.ejoy.core.common.utils.UUIDUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.redisson.api.RLiveObjectService;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//import tk.mybatis.mapper.common.Mapper;
//import tk.mybatis.mapper.entity.Example;
//
//import java.util.Map;
//import java.util.Objects;
//
///**
// * 监控在字段变化处理器
// *
// * @author: chippy
// * @datetime 2021/2/18 23:04
// */
//@Aspect
//@Slf4j
//public class MonitorFieldVariationProcessor implements ApplicationEventPublisherAware {
//
//    private ApplicationEventPublisher applicationEventPublisher;
//    private RLiveObjectService liveObjectService;
//    private Boolean isOpenTrace;
//
//    public MonitorFieldVariationProcessor(RLiveObjectService liveObjectService) {
//        this.liveObjectService = liveObjectService;
//        this.isOpenTrace = isOpenTrace;
//    }
//
//    @Override
//    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//
//    @Pointcut("@annotation(com.ejoy.tkmapper.MethodMonitor)")
//    private void taskPointCut() {
//    }
//
//    @After("taskPointCut()")
//    public void after(ProceedingJoinPoint joinPoint) throws Throwable {
//        final String className = joinPoint.getTarget().getClass().getName();
//        final MethodSignature signature = (MethodSignature)joinPoint.getSignature();
//        final String monitorClassFullPath = MonitorClassDefinition.get(className + signature.getMethod().getName());
//        if (Objects.isNull(monitorClassFullPath)) {
//            joinPoint.proceed();
//            return;
//        }
//        final MonitorFieldDefinition monitorFieldDefinition =
//            liveObjectService.get(MonitorFieldDefinition.class, monitorClassFullPath);
//        liveObjectService.get()
//        if (Objects.isNull(monitorFieldDefinition)) {
//            joinPoint.proceed();
//            return;
//        }
//
//        final Mapper mapper =
//            CommonSpringContext.getApplicationContext().getBean(Mapper.class, monitorFieldDefinition.getMapper());
//        Example example = new Example(monitorFieldDefinition.getMonitorClass());
//        example.createCriteria()
//            .andEqualTo(monitorFieldDefinition.getFieldPrimaryKey(), monitorFieldDefinition.getFieldPrimaryKeyValue());
//        final Object object = mapper.selectOneByExample(example);
//        final Map<String, Object> stringObjectMap = BeanUtil.beanToMap(object);
//        final Object selectFieldValue = stringObjectMap.get(monitorFieldDefinition.getMonitorField());
//        if (!Objects.equals(selectFieldValue, monitorFieldDefinition.getMonitorFieldValue())) {
//            if (isOpenTrace) {
//                OperationLogInfo operationLogInfo = new OperationLogInfo();
//                String fieldPrimaryKeyValue = monitorFieldDefinition.getMonitorFullPath() + "_" + monitorFieldDefinition
//                    .getFieldPrimaryKeyValue();
//                String desc = "主键字段[" + monitorFieldDefinition.getFieldPrimaryKey() + ":" + monitorFieldDefinition
//                    .getFieldPrimaryKeyValue() + "]监控字段[" + monitorFieldDefinition.getMonitorField() + "]值从["
//                    + monitorFieldDefinition.getMonitorFieldValue() + "]变更为[" + selectFieldValue + "]";
//                operationLogInfo.setId(UUIDUtil.generateUuid());
//                operationLogInfo.setMonitorField(monitorFieldDefinition.getMonitorField());
//                operationLogInfo.setMonitorFieldValue(monitorFieldDefinition.getMonitorFieldValue());
//                operationLogInfo.setMonitorPrimaryKeyField(monitorFieldDefinition.getFieldPrimaryKey());
//                operationLogInfo.setMonitorPrimaryKeyFieldValue(fieldPrimaryKeyValue);
//                operationLogInfo.setUpdateDateTime(new DateTime().toStringDefaultTimeZone());
//                operationLogInfo.setDesc(desc);
//                liveObjectService.merge(operationLogInfo);
//                applicationEventPublisher.publishEvent(new MethodMonitorEvent(operationLogInfo));
//            }
//            // 更新键控值
//            monitorFieldDefinition.setMonitorFieldValue(String.valueOf(selectFieldValue));
//        }
//    }
//
//}
