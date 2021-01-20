package com.ejoy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * {@link Long}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class LongRedisSerializer implements RedisSerializer<Long> {

    private static final long DEFAULT_VALUE = 0L;

    @Override
    public byte[] serialize(Long value) throws SerializationException {
        if (Objects.isNull(value)) {
            value = DEFAULT_VALUE;
        }
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Long deserialize(byte[] bytes) throws SerializationException {
        if (Objects.isNull(bytes)) {
            return DEFAULT_VALUE;
        }
        final String resultStr = new String(bytes, StandardCharsets.UTF_8);
        if (resultStr.indexOf("\"") > 0) {
            final String replacedResultStr = resultStr.replaceAll("\"", "");
            return Long.valueOf(replacedResultStr);
        }
        return Long.valueOf(resultStr);
    }

}
