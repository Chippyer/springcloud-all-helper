package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * {@link Short}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class ShortRedisSerializer implements RedisSerializer<Short> {

    @Override
    public byte[] serialize(Short value) throws SerializationException {
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Short deserialize(byte[] bytes) throws SerializationException {
        return Short.valueOf(new String(bytes, StandardCharsets.UTF_8));
    }

}
