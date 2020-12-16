package com.chippy.redis.configuration;

import com.chippy.redis.redisson.exception.RedissonCreateException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Redisson配置类
 *
 * @author: chippy
 * @datetime 2020-12-16 16:12
 */
@Configuration
public class RedissonConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public RedissonClient redissonClient() throws IOException {
        final Environment environment = applicationContext.getEnvironment();
        final String redissonYmlFileName = environment.getProperty("spring.redis.redisson-file");
        if (null == redissonYmlFileName) {
            throw new RedissonCreateException("配置文件中缺少[spring.redis.redisson-file]配置项，导致无法找到redisson配置文件");
        }
        Config config = Config.fromYAML(new File(redissonYmlFileName));
        return Redisson.create(config);
    }

}
