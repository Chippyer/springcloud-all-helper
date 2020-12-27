package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * {@link Integer}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class IntegerRedisSerializer implements RedisSerializer<Integer> {

    private static final int DEFAULT_VALUE = 0;

    @Override
    public byte[] serialize(Integer value) throws SerializationException {
        if (Objects.isNull(value)) {
            value = DEFAULT_VALUE;
        }
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Integer deserialize(byte[] bytes) throws SerializationException {
        if (Objects.isNull(bytes)) {
            return DEFAULT_VALUE;
        }
        final String resultStr = new String(bytes, StandardCharsets.UTF_8);
        if (resultStr.indexOf("\"") > 0) {
            final String replacedResultStr = resultStr.replaceAll("\"", "");
            return Integer.valueOf(replacedResultStr);
        }
        return Integer.valueOf(resultStr);
    }

}
