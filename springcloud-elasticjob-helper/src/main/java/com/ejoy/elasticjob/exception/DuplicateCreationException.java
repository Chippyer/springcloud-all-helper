package com.ejoy.elasticjob.exception;

/**
 * 重复创建相同任务异常
 *
 * @author: chippy
 * @datetime 2020-12-24 17:10
 */
public class DuplicateCreationException extends RuntimeException {

    public DuplicateCreationException(String message) {
        super(message);
    }

}
