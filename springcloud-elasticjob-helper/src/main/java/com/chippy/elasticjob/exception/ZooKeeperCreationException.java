package com.chippy.elasticjob.exception;

/**
 * ZopKeeper创建实例异常
 *
 * @author: chippy
 * @datetime 2020-11-21 13:26
 */
public class ZooKeeperCreationException extends RuntimeException {

    public ZooKeeperCreationException(String message) {
        super(message);
    }
}
