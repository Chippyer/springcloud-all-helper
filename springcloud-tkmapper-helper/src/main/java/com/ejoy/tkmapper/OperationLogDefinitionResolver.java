package com.ejoy.tkmapper;

import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.ReflectUtil;
import com.ejoy.core.common.utils.AnnotationUtils;
import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.core.common.utils.ObjectsUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.spring.annotation.MapperScan;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 记录操作日志信息解析器
 *
 * @author: chippy
 * @datetime 2021-02-18 18:13
 */
@Slf4j
public class OperationLogDefinitionResolver implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    private List<String> scannerPackages = new ArrayList<>();
    private RLiveObjectService liveObjectService;

    public OperationLogDefinitionResolver(RLiveObjectService liveObjectService) {
        this.liveObjectService = liveObjectService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void init() {
        // 获取MapperScan注解
        final Map<String, Object> beansWithAnnotations = applicationContext.getBeansWithAnnotation(MapperScan.class);
        if (CollectionsUtils.isEmpty(beansWithAnnotations)) {
            throw new ResolverOperationLogDefinitionException("请指定监控扫描数据的实体所在报名");
        }
        beansWithAnnotations.forEach((k, v) -> {
            final MapperScan mapperScan = AnnotationUtils.findAnnotation(v.getClass(), MapperScan.class);
            final String[] annotationScannerPackages = mapperScan.value();
            if (ObjectsUtil.isNotEmpty(annotationScannerPackages)) {
                scannerPackages.addAll(Arrays.asList(annotationScannerPackages));
            }
        });

        // 扫描出基本信息
        for (String scannerPackage : scannerPackages) {
            final Set<Class<?>> scannerClasses = ClassScanner.scanPackage(scannerPackage);
            for (Class<?> scannerClass : scannerClasses) {
                final Mapper mapper = applicationContext.getBean(Mapper.class, scannerClass);
                final Class<?> monitorClass = getInterfaceT(mapper, 0);
                final Field[] monitorDeclaredFields = monitorClass.getDeclaredFields();
                final OperationLogInfo operationLogInfo = new OperationLogInfo(monitorClass, mapper);
                for (Field monitorField : monitorDeclaredFields) {
                    final Monitor monitorAnnotation = AnnotationUtils.findAnnotation(monitorField, Monitor.class);
                    final Id idAnnotation = AnnotationUtils.findAnnotation(monitorField, Id.class);
                    if (Objects.isNull(monitorAnnotation) && Objects.isNull(idAnnotation)) {
                        continue;
                    }
                    final String fieldName = monitorField.getName();
                    if (Objects.nonNull(monitorAnnotation)) {
                        operationLogInfo.setMonitorField(fieldName);
                    }
                    if (Objects.nonNull(idAnnotation)) {
                        String id = monitorClass.getName() + fieldName;
                        operationLogInfo.setMonitorPrimaryKeyField(fieldName);
                        operationLogInfo.setId(id);
                    }
                }
                if (Objects.isNull(operationLogInfo.getMonitorField()) || Objects
                    .isNull(operationLogInfo.getMonitorPrimaryKeyField())) {
                    continue;
                }
                // 初始化监控数据集合
                final List list = mapper.selectAll();
                if (CollectionsUtils.isNotEmpty(list)) {
                    for (Object o : list) {
                        OperationLogInfo mergeOperationLogInfo = new OperationLogInfo();
                        BeanUtils.copyProperties(operationLogInfo, mergeOperationLogInfo);
                        final Object monitorFieldValue =
                            ReflectUtil.getFieldValue(o, operationLogInfo.getMonitorField());
                        final Object monitorPrimaryKeyFieldValue =
                            ReflectUtil.getFieldValue(o, operationLogInfo.getMonitorPrimaryKeyField());
                        mergeOperationLogInfo.setMonitorFieldValue(String.valueOf(monitorFieldValue));
                        mergeOperationLogInfo
                            .setMonitorPrimaryKeyFieldValue(String.valueOf(monitorPrimaryKeyFieldValue));
                        MonitorClassDefinition.register(operationLogInfo.getMonitorFullPath(),
                            operationLogInfo.getMonitorPrimaryKeyField());
                        liveObjectService.merge(mergeOperationLogInfo);
                    }
                }
            }
        }
    }

    /**
     * 获取接口上的泛型T * * @param o 接口 * @param index 泛型索引
     */
    public static Class<?> getInterfaceT(Object o, int index) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType)types[index];
        Type type = parameterizedType.getActualTypeArguments()[index];
        return checkType(type, index);
    }

    private static Class<?> checkType(Type type, int index) {
        if (type instanceof Class<?>) {
            return (Class<?>)type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            Type t = pt.getActualTypeArguments()[index];
            return checkType(t, index);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException(
                "Expected a Class, ParameterizedType" + ", but <" + type + "> is of type " + className);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

}
