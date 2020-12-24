package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * {@link Integer}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class IntegerRedisSerializer implements RedisSerializer<Integer> {

    @Override
    public byte[] serialize(Integer value) throws SerializationException {
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Integer deserialize(byte[] bytes) throws SerializationException {
        return Integer.valueOf(new String(bytes, StandardCharsets.UTF_8));
    }

}
