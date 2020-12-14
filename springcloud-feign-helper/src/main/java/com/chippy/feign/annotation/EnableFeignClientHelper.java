package com.chippy.feign.annotation;

import com.chippy.feign.configuration.FeignClientHelperAutoConfiguration;
import com.chippy.feign.support.definition.FeignClientDefinitionResolver;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启FeignClientHelper辅助类注解
 *
 * @author: chippy
 * @datetime: 2020-11-21 13:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FeignClientHelperAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableFeignClientHelper {

    Class<? extends FeignClientDefinitionResolver> resolver() default FeignClientDefinitionResolver.class;

}
