package com.chippy.feign.exception;

/**
 * FeignClientHelper使用时调度异常定义
 *
 * @author: chippy
 */
public class FastClientInvokeException extends RuntimeException {

    private int code = 200;

    public FastClientInvokeException(String message) {
        super(message);
    }

    public FastClientInvokeException(String message, int code) {
        super(message);
        this.code = code;
    }

}
