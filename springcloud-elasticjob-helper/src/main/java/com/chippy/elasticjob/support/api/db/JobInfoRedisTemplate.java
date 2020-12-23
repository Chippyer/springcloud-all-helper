package com.chippy.elasticjob.support.api.db;

import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * JobInfo RedisTemplate impl
 *
 * @author: chippy
 * @datetime 2020-12-18 16:34
 */
public class JobInfoRedisTemplate extends RedisTemplate<String, JobInfo> {

    public JobInfoRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<JobInfo> jobInfoSerializer = new JobInfoRedisSerializer();
        setKeySerializer(stringSerializer);
        setValueSerializer(jobInfoSerializer);
        setHashKeySerializer(stringSerializer);
        setHashValueSerializer(jobInfoSerializer);
    }

    public JobInfoRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return new DefaultStringRedisConnection(connection);
    }

}
