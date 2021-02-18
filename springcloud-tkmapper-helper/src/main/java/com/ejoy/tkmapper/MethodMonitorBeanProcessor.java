package com.ejoy.tkmapper;

import com.ejoy.core.common.utils.AnnotationUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 方法监控器相关Bean初始化信息收集
 *
 * @author: chippy
 * @datetime 2021/2/19 1:07
 */
public class MethodMonitorBeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> clazz = bean.getClass();
        final String className = clazz.getName();
        final Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            final MethodMonitor annotation = AnnotationUtils.findAnnotation(declaredMethod, MethodMonitor.class);
            if (Objects.nonNull(annotation)) {
                String invokeMethodId = className + declaredMethod.getName();
                MonitorClassDefinition.register(invokeMethodId, annotation.value());
            }
        }
        return bean;
    }

}
