package com.chippy.redis.support.api;

import com.chippy.redis.support.seializer.BooleanRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * value为{@link Boolean}类型的RedisTemplate
 *
 * @author: chippy
 * @datetime 2020/12/24 23:31
 */
public class BooleanRedisTemplate extends RedisTemplate<String, Boolean> {

    public BooleanRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Boolean> booleanRedisSerializer = new BooleanRedisSerializer();

        setKeySerializer(stringSerializer);
        setValueSerializer(booleanRedisSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(booleanRedisSerializer);
    }

    public BooleanRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
