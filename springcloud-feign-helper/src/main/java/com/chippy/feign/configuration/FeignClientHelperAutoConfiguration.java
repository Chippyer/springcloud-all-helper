package com.chippy.feign.configuration;

import cn.hutool.core.util.ReflectUtil;
import com.chippy.feign.annotation.EnableFeignClientHelper;
import com.chippy.feign.support.definition.FeignClientDefinitionResolver;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * FeignClientHelper自动注入配置类
 *
 * @author chippy
 */
@Configuration
@AutoConfigureBefore(FeignAutoConfiguration.class)
@ComponentScan({"com.chippy.feign"})
public class FeignClientHelperAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public FeignClientDefinitionResolver feignClientDefinitionResolver() {
        Map<String, Object> withEnableSpringCloudHelperClasses =
            applicationContext.getBeansWithAnnotation(EnableFeignClientHelper.class);
        Iterator<String> iterator = withEnableSpringCloudHelperClasses.keySet().iterator();
        // 只认第一个使用注解的地方
        EnableFeignClientHelper annotationInfo = AnnotationUtils
            .findAnnotation(withEnableSpringCloudHelperClasses.get(iterator.next()).getClass(),
                EnableFeignClientHelper.class);
        Class<? extends FeignClientDefinitionResolver> resolver = annotationInfo.resolver();
        return ReflectUtil.newInstance(resolver);
    }

}
