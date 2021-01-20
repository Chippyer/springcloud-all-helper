package com.ejoy.redis.support.api;

import com.ejoy.redis.support.seializer.DoubleRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * value为{@link Double}类型的RedisTemplate
 *
 * @author: chippy
 * @datetime 2020/12/24 23:31
 */
public class DoubleRedisTemplate extends RedisTemplate<String, Double> {

    public DoubleRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Double> doubleRedisSerializer = new DoubleRedisSerializer();

        setKeySerializer(stringSerializer);
        setValueSerializer(doubleRedisSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(doubleRedisSerializer);
    }

    public DoubleRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
