package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * {@link Long}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class LongRedisSerializer implements RedisSerializer<Long> {

    @Override
    public byte[] serialize(Long value) throws SerializationException {
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Long deserialize(byte[] bytes) throws SerializationException {
        return Long.valueOf(new String(bytes, StandardCharsets.UTF_8));
    }

}
