package com.chippy.elasticjob.support.api.db;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * {@link JobInfo} 实现Redis序列化
 *
 * @author: chippy
 * @datetime 2020-12-18 16:38
 */
public class JobInfoRedisSerializer implements RedisSerializer<JobInfo> {
    @Override
    public byte[] serialize(JobInfo jobInfo) throws SerializationException {
        return (Objects.isNull(jobInfo) ? null : JSONUtil.toJsonStr(jobInfo).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public JobInfo deserialize(byte[] bytes) throws SerializationException {
        if (Objects.isNull(bytes)) {
            return null;
        }
        return JSONUtil.toBean(new String(bytes, StandardCharsets.UTF_8), JobInfo.class);
    }
}
