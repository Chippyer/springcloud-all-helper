package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * {@link Double}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class DoubleRedisSerializer implements RedisSerializer<Double> {

    @Override
    public byte[] serialize(Double value) throws SerializationException {
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Double deserialize(byte[] bytes) throws SerializationException {
        return Double.valueOf(new String(bytes, StandardCharsets.UTF_8));
    }

}
