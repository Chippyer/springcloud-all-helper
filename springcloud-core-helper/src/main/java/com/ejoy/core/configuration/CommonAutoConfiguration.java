package com.ejoy.core.configuration;

import com.ejoy.core.common.utils.CommonSpringContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonAutoConfiguration {

    @Bean
    public CommonSpringContext commonSpringContext() {
        return new CommonSpringContext();
    }

}
