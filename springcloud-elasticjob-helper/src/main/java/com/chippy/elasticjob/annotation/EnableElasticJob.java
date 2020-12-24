package com.chippy.elasticjob.annotation;

import com.chippy.elasticjob.configuration.ElasticJobAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启Elastic-Job注释
 *
 * @author: chippy
 * @datetime: 2020-11-21 13:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ElasticJobAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableElasticJob {

    boolean traceMonitor() default true;

}
