package com.ejoy.redis.support.api;

import com.ejoy.redis.support.seializer.ShortRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * value为{@link Short}类型的RedisTemplate
 *
 * @author: chippy
 * @datetime 2020/12/24 23:31
 */
public class ShortRedisTemplate extends RedisTemplate<String, Short> {

    public ShortRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Short> shortRedisSerializer = new ShortRedisSerializer();

        setKeySerializer(stringSerializer);
        setValueSerializer(shortRedisSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(shortRedisSerializer);
    }

    public ShortRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
