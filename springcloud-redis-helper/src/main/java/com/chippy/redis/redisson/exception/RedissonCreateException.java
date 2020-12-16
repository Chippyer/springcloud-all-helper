package com.chippy.redis.redisson.exception;

/**
 * Redisson创建过程中的异常定义
 *
 * @author: chippy
 * @datetime 2020/12/16 22:58
 */
public class RedissonCreateException extends RuntimeException {

    public RedissonCreateException(String errorMsg) {
        super(errorMsg);
    }

}
