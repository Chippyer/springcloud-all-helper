package com.ejoy.tkmapper;

import cn.hutool.core.bean.BeanUtil;
import com.ejoy.core.common.utils.CommonSpringContext;
import com.ejoy.core.common.utils.ObjectsUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLiveObjectService;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Map;
import java.util.Objects;

/**
 * 监控在字段变化处理器
 *
 * @author: chippy
 * @datetime 2021/2/18 23:04
 */
@Aspect
@Slf4j
public class MonitorFieldVariationProcessor {

    private RLiveObjectService liveObjectService;

    public MonitorFieldVariationProcessor(RLiveObjectService liveObjectService) {
        this.liveObjectService = liveObjectService;
    }

    @Pointcut("@annotation(com.ejoy.tkmapper.MethodMonitor)")
    private void taskPointCut() {
    }

    @After("taskPointCut()")
    public void after(ProceedingJoinPoint joinPoint) throws Throwable {
        final String className = joinPoint.getTarget().getClass().getName();
        final MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        final String monitorClassFullPath = MonitorClassDefinition.get(className + signature.getMethod().getName());
        if (Objects.isNull(monitorClassFullPath)) {
            joinPoint.proceed();
            return;
        }
        final MonitorFieldDefinition monitorFieldDefinition =
            liveObjectService.get(MonitorFieldDefinition.class, monitorClassFullPath);
        if (Objects.isNull(monitorFieldDefinition)) {
            joinPoint.proceed();
            return;
        }
        final Mapper mapper =
            CommonSpringContext.getApplicationContext().getBean(Mapper.class, monitorFieldDefinition.getMapper());
        Example example = new Example(monitorFieldDefinition.getMonitorClass());
        example.createCriteria()
            .andEqualTo(monitorFieldDefinition.getFieldPrimaryKey(), monitorFieldDefinition.getFieldPrimaryKeyValue());
        final Object object = mapper.selectOneByExample(example);
        final Map<String, Object> stringObjectMap = BeanUtil.beanToMap(object);
        final Object selectFieldValue = stringObjectMap.get(monitorFieldDefinition.getField());
        if (!Objects.equals(selectFieldValue, monitorFieldDefinition.getFieldValue())) {
            // 存储日志信息or发送事件
        }
    }

}
