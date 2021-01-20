package com.ejoy.feign.exception;

/**
 * FeignClientDefinition创建时异常
 *
 * @author: chippy
 * @datetime 2020/12/14 23:13
 */
public class FeignClientDefinitionCreateException extends RuntimeException {

    public FeignClientDefinitionCreateException(String errorMsg) {
        super(errorMsg);
    }

}
