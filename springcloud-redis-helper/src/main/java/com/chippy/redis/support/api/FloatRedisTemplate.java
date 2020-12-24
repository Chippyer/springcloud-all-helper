package com.chippy.redis.support.api;

import com.chippy.redis.support.seializer.FloatRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * value为{@link Float}类型的RedisTemplate
 *
 * @author: chippy
 * @datetime 2020/12/24 23:31
 */
public class FloatRedisTemplate extends RedisTemplate<String, Float> {

    public FloatRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Float> floatRedisSerializer = new FloatRedisSerializer();

        setKeySerializer(stringSerializer);
        setValueSerializer(floatRedisSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(floatRedisSerializer);
    }

    public FloatRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
