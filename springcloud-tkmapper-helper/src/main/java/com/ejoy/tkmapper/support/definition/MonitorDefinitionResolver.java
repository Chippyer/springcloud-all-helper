package com.ejoy.tkmapper.support.definition;

import cn.hutool.core.lang.ClassScanner;
import com.ejoy.core.common.constants.GlobalConstantEnum;
import com.ejoy.core.common.utils.AnnotationUtils;
import com.ejoy.core.common.utils.ObjectsUtil;
import com.ejoy.tkmapper.annotation.Monitor;
import com.ejoy.tkmapper.annotation.MonitorExecutor;
import com.ejoy.tkmapper.exception.MonitorClassDefinitionResolverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import tk.mybatis.mapper.common.Mapper;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 记录操作日志信息解析器
 *
 * @author: chippy
 * @datetime 2021-02-18 18:13
 */
@Slf4j
public class MonitorDefinitionResolver implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    List<String> scannerPackages = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void init() {
        this.doInitServer();
        this.doInitScannerPackages();
        this.doInitMonitorInfo();
    }

    private void doInitMonitorInfo() {
        final MonitorClassDefinition monitorClassDefinition = MonitorClassDefinition.getInstance();
        for (String scannerPackage : scannerPackages) {
            final Set<Class<?>> scannerClasses = ClassScanner.scanPackage(scannerPackage);
            for (Class<?> monitorClass : scannerClasses) {
                final MonitorExecutor monitorDataExecutor =
                    AnnotationUtils.findAnnotation(monitorClass, MonitorExecutor.class);
                if (Objects.isNull(monitorDataExecutor)) {
                    continue;
                }
                final Mapper mapper = applicationContext.getBean(Mapper.class, monitorDataExecutor.value());
                final Field[] monitorDeclaredFields = monitorClass.getDeclaredFields();
                final MonitorClassDefinition.Element element = new MonitorClassDefinition.Element(monitorClass, mapper);
                this.doResolveFields(monitorDeclaredFields, element);
                monitorClassDefinition.register(element);
            }
        }
    }

    private void doInitServer() {
        final String server = applicationContext.getEnvironment()
            .getProperty(GlobalConstantEnum.SPRING_APPLICATION_NAME.getConstantValue());
        MonitorClassDefinition.getInstance().setServer(server);
    }

    private void doInitScannerPackages() {
        final String monitorScannerPackages =
            applicationContext.getEnvironment().getProperty(GlobalConstantEnum.MONITOR_PACKAGE.getConstantValue());
        if (ObjectsUtil.isEmpty(monitorScannerPackages)) {
            throw new MonitorClassDefinitionResolverException("请指定监控扫描数据的实体所在包名，多个以','号分割");
        }
        scannerPackages.addAll(Arrays.asList(monitorScannerPackages.split(",")));
    }

    private void doResolveFields(Field[] monitorDeclaredFields, MonitorClassDefinition.Element element) {
        for (Field monitorField : monitorDeclaredFields) {
            final Monitor monitorAnnotation = AnnotationUtils.findAnnotation(monitorField, Monitor.class);
            final Id idAnnotation = AnnotationUtils.findAnnotation(monitorField, Id.class);
            if (Objects.isNull(monitorAnnotation) && Objects.isNull(idAnnotation)) {
                continue;
            }
            final String fieldName = monitorField.getName();
            if (Objects.nonNull(monitorAnnotation)) {
                element.getMonitorFields().add(fieldName);
                element.setCustomerProcess(monitorAnnotation.value());
                element.setExpireTime(monitorAnnotation.expire());
            }
            if (Objects.nonNull(idAnnotation)) {
                element.setPrimaryKeyField(fieldName);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

}
