package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * {@link Float}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class FloatRedisSerializer implements RedisSerializer<Float> {

    @Override
    public byte[] serialize(Float value) throws SerializationException {
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Float deserialize(byte[] bytes) throws SerializationException {
        return Float.valueOf(new String(bytes, StandardCharsets.UTF_8));
    }

}
