package com.ejoy.tkmapper.annotation;

import com.ejoy.tkmapper.configuration.MonitorFieldAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启监控功能注解
 *
 * @author: chippy
 * @datetime: 2020-11-21 13:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MonitorFieldAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableMonitor {
}
