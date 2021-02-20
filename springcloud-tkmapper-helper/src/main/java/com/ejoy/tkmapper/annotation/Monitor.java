package com.ejoy.tkmapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 监控字段标识
 *
 * @author: chippy
 * @datetime 2021/2/18 22:52
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {

    /**
     * 是否自定义处理
     * 如果值为false则进行默认的信息记录，默认为false
     * 如果值为true则发送${@link com.ejoy.tkmapper.support.event.MonitorEvent}事件供自定义处理
     *
     * @return boolean
     * @author chippy
     */
    boolean value() default false;

}
