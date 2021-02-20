package com.ejoy.tkmapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类型监控注解
 *
 * @author: chippy
 * @datetime: 2021-02-20 11:00
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MonitorExecutor {

    /**
     * 数据执行器
     *
     * @return java.lang.Class
     * @author chippy
     */
    Class executor();

}
