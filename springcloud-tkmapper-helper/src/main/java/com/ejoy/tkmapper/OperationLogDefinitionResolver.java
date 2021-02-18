package com.ejoy.tkmapper;

import cn.hutool.core.lang.ClassScanner;
import com.ejoy.core.common.utils.AnnotationUtils;
import com.ejoy.core.common.utils.CollectionsUtils;
import com.ejoy.core.common.utils.CommonSpringContext;
import com.ejoy.core.common.utils.ObjectsUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
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
                final Mapper mapper = CommonSpringContext.getApplicationContext().getBean(Mapper.class, scannerClass);
                // todo 暂时不考虑这里是否能够获取到泛型类型
                final Type monitorType =
                    ((ParameterizedType)scannerClass.getGenericSuperclass()).getActualTypeArguments()[0];
                final Class<? extends Type> monitorClass = monitorType.getClass();
                final Field[] monitorDeclaredFields = monitorClass.getDeclaredFields();
                final MonitorFieldDefinition monitorFieldDefinition = new MonitorFieldDefinition(monitorClass, mapper);
                for (Field monitorField : monitorDeclaredFields) {
                    final Monitor monitorAnnotation = AnnotationUtils.findAnnotation(monitorField, Monitor.class);
                    final Id idAnnotation = AnnotationUtils.findAnnotation(monitorField, Id.class);
                    if (Objects.isNull(monitorAnnotation) && Objects.isNull(idAnnotation)) {
                        continue;
                    }
                    final String fieldName = monitorField.getName();
                    if (Objects.nonNull(monitorAnnotation)) {
                        monitorFieldDefinition.setField(fieldName);
                    }
                    if (Objects.nonNull(idAnnotation)) {
                        monitorFieldDefinition.setFieldPrimaryKey(fieldName);
                    }
                }
                liveObjectService.merge(monitorFieldDefinition);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

}
