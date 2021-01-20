package com.ejoy.redis.support.api;

import com.ejoy.redis.support.seializer.IntegerRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * value为{@link Integer}类型的RedisTemplate
 *
 * @author: chippy
 * @datetime 2020/12/24 23:31
 */
public class IntegerRedisTemplate extends RedisTemplate<String, Integer> {

    public IntegerRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Integer> integerRedisSerializer = new IntegerRedisSerializer();

        setKeySerializer(stringSerializer);
        setValueSerializer(integerRedisSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(integerRedisSerializer);
    }

    public IntegerRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
