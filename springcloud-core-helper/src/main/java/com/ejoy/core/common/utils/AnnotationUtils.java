package com.ejoy.core.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationConfigurationException;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 基于Spring容器的注解工具类
 *
 * @author: chippy
 * @datetime 2020-12-11 12:35
 */
public class AnnotationUtils extends org.springframework.core.annotation.AnnotationUtils {

    public static <T extends Annotation> T getFirstAnnotation(ApplicationContext applicationContext, Class<T> clazz) {
        final Map withAnnotationEnableElasticJob = applicationContext.getBeansWithAnnotation(clazz);
        if (ObjectsUtil.isEmpty(withAnnotationEnableElasticJob)) {
            throw new AnnotationConfigurationException("请显示使用[" + clazz.getName() + "]注解");
        }
        Iterator iterator = withAnnotationEnableElasticJob.keySet().iterator();
        final T annotation = org.springframework.core.annotation.AnnotationUtils
            .findAnnotation(withAnnotationEnableElasticJob.get(iterator.next()).getClass(), clazz);
        if (Objects.isNull(annotation)) {
            throw new AnnotationConfigurationException("请显示使用[" + clazz.getName() + "]注解");
        }
        return annotation;
    }

}
