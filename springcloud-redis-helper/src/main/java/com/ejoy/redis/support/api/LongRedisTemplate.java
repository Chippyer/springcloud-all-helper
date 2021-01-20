package com.ejoy.redis.support.api;

import com.ejoy.redis.support.seializer.LongRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * value为{@link Long}类型的RedisTemplate
 *
 * @author: chippy
 * @datetime 2020/12/24 23:31
 */
public class LongRedisTemplate extends RedisTemplate<String, Long> {

    public LongRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Long> longSerializer = new LongRedisSerializer();

        setKeySerializer(stringSerializer);
        setValueSerializer(longSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(longSerializer);
    }

    public LongRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
