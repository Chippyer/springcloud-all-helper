package com.ejoy.tkmapper;

import cn.hutool.core.lang.ClassScanner;
import com.ejoy.core.common.constants.GlobalConstantEnum;
import com.ejoy.core.common.utils.AnnotationUtils;
import com.ejoy.core.common.utils.ObjectsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import tk.mybatis.mapper.common.Mapper;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 记录操作日志信息解析器
 *
 * @author: chippy
 * @datetime 2021-02-18 18:13
 */
@Slf4j
public class OperationLogDefinitionResolver implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void init() {
        final List<String> scannerPackages = this.doInitScannerPackages();
        // 扫描出基本信息
        for (String scannerPackage : scannerPackages) {
            final Set<Class<?>> scannerClasses = ClassScanner.scanPackage(scannerPackage);
            for (Class<?> monitorClass : scannerClasses) {
                final MonitorExecutor monitorDataExecutor =
                    AnnotationUtils.findAnnotation(monitorClass, MonitorExecutor.class);
                if (Objects.isNull(monitorDataExecutor)) {
                    continue;
                }
                final Mapper mapper = applicationContext.getBean(Mapper.class, monitorDataExecutor.executor());
                final Field[] monitorDeclaredFields = monitorClass.getDeclaredFields();
                final MonitorDefinition monitorDefinition = new MonitorDefinition(monitorClass, mapper);
                for (Field monitorField : monitorDeclaredFields) {
                    final Monitor monitorAnnotation = AnnotationUtils.findAnnotation(monitorField, Monitor.class);
                    final Id idAnnotation = AnnotationUtils.findAnnotation(monitorField, Id.class);
                    if (Objects.isNull(monitorAnnotation) && Objects.isNull(idAnnotation)) {
                        continue;
                    }
                    final String fieldName = monitorField.getName();
                    if (Objects.nonNull(monitorAnnotation)) {
                        monitorDefinition.setMonitorField(fieldName);
                    }
                    if (Objects.nonNull(idAnnotation)) {
                        monitorDefinition.setPrimaryKeyField(fieldName);
                    }
                }
                MonitorClassDefinition.register(monitorDefinition);
                // if (Objects.isNull(monitorDefinition.getField()) || Objects
                //     .isNull(monitorDefinition.getPrimaryKeyField())) {
                //     continue;
                // }
                // 初始化监控数据集合
                // final List list = mapper.selectAll();
                // if (CollectionsUtils.isNotEmpty(list)) {
                //     for (Object o : list) {
                //         final Object monitorFieldValue = ReflectUtil.getFieldValue(o, monitorDefinition.getField());
                //         final Object monitorPrimaryKeyFieldValue =
                //             ReflectUtil.getFieldValue(o, monitorDefinition.getPrimaryKeyField());
                //         String operationLogInfoId =
                //             monitorDefinition.getMonitorFullPath() + "_" + monitorPrimaryKeyFieldValue;
                //         final MonitorDefinition existsOperationLogInfo =
                //             liveObjectService.get(MonitorDefinition.class, operationLogInfoId);
                //         if (Objects.nonNull(existsOperationLogInfo)) {
                //             this.doUpdate(existsOperationLogInfo, monitorFieldValue);
                //         } else {
                //             this.doInsert(monitorDefinition, operationLogInfoId, monitorFieldValue,
                //                 monitorPrimaryKeyFieldValue);
                //         }
                //     }
                // }
            }
        }
    }

    // private void doUpdate(MonitorDefinition existsOperationLogInfo, Object monitorFieldValue) {
    //     if (!existsOperationLogInfo.getFieldValue().equals(monitorFieldValue)) {
    //         existsOperationLogInfo.setFieldValue(String.valueOf(monitorFieldValue));
    //     }
    // }
    //
    // private void doInsert(MonitorDefinition operationLogInfo, String id, Object monitorFieldValue,
    //     Object monitorPrimaryKeyFieldValue) {
    //     MonitorDefinition mergeOperationLogInfo = new MonitorDefinition();
    //     BeanUtils.copyProperties(operationLogInfo, mergeOperationLogInfo);
    //     mergeOperationLogInfo.setId(id);
    //     mergeOperationLogInfo.setFieldValue(String.valueOf(monitorFieldValue));
    //     mergeOperationLogInfo.setPrimaryKeyFieldValue(String.valueOf(monitorPrimaryKeyFieldValue));
    //     mergeOperationLogInfo.setCreateDateTime(new DateTime().toStringDefaultTimeZone());
    //     mergeOperationLogInfo.setDesc("初始化监控数据信息");
    //     MonitorClassDefinition
    //         .register(mergeOperationLogInfo.getMonitorFullPath(), mergeOperationLogInfo.getPrimaryKeyField());
    //     liveObjectService.merge(mergeOperationLogInfo);
    // }

    private List<String> doInitScannerPackages() {
        final String monitorScannerPackages =
            applicationContext.getEnvironment().getProperty(GlobalConstantEnum.MONITOR_PACKAGE.getConstantValue());
        if (ObjectsUtil.isEmpty(monitorScannerPackages)) {
            throw new ResolverOperationLogDefinitionException("请指定监控扫描数据的实体所在包名，多个以','号分割");
        }
        return Arrays.asList(monitorScannerPackages.split(","));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

}
