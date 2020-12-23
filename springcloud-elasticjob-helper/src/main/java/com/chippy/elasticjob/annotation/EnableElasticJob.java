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

    /**
     * 任务跟踪使用的DB类型，默认Redis
     */
    DBTypeEnum traceDbType() default DBTypeEnum.REDIS;

}
