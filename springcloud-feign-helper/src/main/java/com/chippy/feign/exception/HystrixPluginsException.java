package com.chippy.feign.exception;

/**
 * Hystrix插件异常
 *
 * @author: chippy
 * @datetime 2021-01-12 17:14
 */
public class HystrixPluginsException extends RuntimeException {

    public HystrixPluginsException(String errorMsg) {
        super(errorMsg);
    }

}
