package com.chippy.redis.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * Redisson配置类
 *
 * @author: chippy
 * @datetime 2020-12-16 16:12
 */
@Configuration
public class RedissonConfiguration {

    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(new File("redisson.yml"));
        return Redisson.create(config);
    }

    public static void main(String[] args) {
        System.out.println(5 >> 1);
    }

}
