package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * {@link Boolean}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class BooleanRedisSerializer implements RedisSerializer<Boolean> {

    @Override
    public byte[] serialize(Boolean value) throws SerializationException {
        if (Objects.isNull(value)) {
            value = Boolean.FALSE;
        }
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Boolean deserialize(byte[] bytes) throws SerializationException {
        if (Objects.isNull(bytes)) {
            return Boolean.FALSE;
        }
        final String resultStr = new String(bytes, StandardCharsets.UTF_8);
        if (resultStr.indexOf("\"") > 0) {
            final String replacedResultStr = resultStr.replaceAll("\"", "");
            return Boolean.valueOf(replacedResultStr);
        }
        return Boolean.valueOf(resultStr);
    }

}
