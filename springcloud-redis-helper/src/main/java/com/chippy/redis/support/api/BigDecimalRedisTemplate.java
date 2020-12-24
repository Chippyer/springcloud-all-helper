package com.chippy.redis.support.api;

import com.chippy.redis.support.seializer.BigDecimalRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.math.BigDecimal;

/**
 * value为{@link BigDecimal}类型的RedisTemplate
 *
 * @author: chippy
 * @datetime 2020/12/24 23:31
 */
public class BigDecimalRedisTemplate extends RedisTemplate<String, BigDecimal> {

    public BigDecimalRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<BigDecimal> bigDecimalRedisSerializer = new BigDecimalRedisSerializer();

        setKeySerializer(stringSerializer);
        setValueSerializer(bigDecimalRedisSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(bigDecimalRedisSerializer);
    }

    public BigDecimalRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
