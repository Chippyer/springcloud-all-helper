package com.chippy.redis.support.seializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/**
 * {@link java.math.BigDecimal}类型序列化
 *
 * @author: chippy
 * @datetime 2020/12/24 23:39
 */
public class BigDecimalRedisSerializer implements RedisSerializer<BigDecimal> {

    private int keepDecimal = 2;
    private int bias = BigDecimal.ROUND_DOWN;

    public BigDecimalRedisSerializer() {
    }

    public BigDecimalRedisSerializer(int keepDecimal, int bias) {
        this.keepDecimal = keepDecimal;
        this.bias = bias;
    }

    @Override
    public byte[] serialize(BigDecimal value) throws SerializationException {
        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BigDecimal deserialize(byte[] bytes) throws SerializationException {
        return BigDecimal.valueOf(Double.parseDouble(new String(bytes, StandardCharsets.UTF_8)))
            .setScale(keepDecimal, bias);
    }

}
